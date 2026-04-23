package no.uio.ifi.in2000.team20.team20app.data.datasource

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.encodeBase64
import no.uio.ifi.in2000.team20.team20app.data.api.FrostRoutes
import no.uio.ifi.in2000.team20.team20app.data.api.FrostV1Routs
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0SourceResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostSourceDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostSourceResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ResponseDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Interface for Frost API data fetching.
 *
 * Responsibility:
 * - Define the contract for getting stations and observations
 *
 * Why:
 * Makes FrostDataSource testable by allowing fake implementations in tests.
 * Temperature normals use V0 (frost.met.no) — historical data not yet in V1.
 * Other methods use V1 (frost-rc.met.no) where data is available.
 */
interface FrostDataSourceService {
    suspend fun getTemperatureNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getPrecipitationNormals(lat: Double, lon: Double): FrostObservationResponseDto
    suspend fun getPrecipitationMean(lat: Double, lon: Double): FrostObservationResponseDto
    suspend fun getSnowDepthHistory(lat: Double, lon: Double): FrostObservationResponseDto
    suspend fun getWindHistory(lat: Double, lon: Double): FrostObservationResponseDto
    suspend fun getSunshineNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getStation(lat: Double, lon: Double): FrostSourceDto
    suspend fun getObservations(stationId: String): FrostObservationResponseDto
    suspend fun getRankedObservationsForParcipitation(lat: Double, lon: Double, startYear: Int = 1980, endYear: Int = 2025, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
    suspend fun getRankedObservationsForWind(lat: Double, lon: Double, startYear: Int = 1980, endYear: Int = 2025, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
}

/**
 * Handles remote climate data retrieval from the Frost API.
 *
 * Responsibility:
 * - Call Frost API endpoints (V0 for historical normals, V1 for current data)
 * - Return raw DTO data
 *
 * Why:
 * Abstracts network layer away from the repository layer.
 */
class FrostDataSource(
    private val client: HttpClient,
    private val credentials: String // format: "clientId:clientSecret"
) : FrostDataSourceService {

    private val authHeader: String
        get() {
            val header = "Basic " + credentials.encodeBase64()
            Log.d("FrostDataSource", "credentials empty=${credentials.isEmpty()}, header length=${header.length}, header prefix=${header.take(12)}")
            return header
        }

    // Builds the V1 nearest-station proximity JSON for the `nearest` parameter
    // Uses Locale.US to ensure '.' as decimal separator regardless of device locale
    // TODO: create fallback if no stations are found? look into this
    private fun nearestParam(lat: Double, lon: Double, maxCount: Int = 3, maxDist: Int = 50): String {
        val lonStr = String.format(java.util.Locale.US, "%.4f", lon)
        val latStr = String.format(java.util.Locale.US, "%.4f", lat)
        return """{"maxdist":$maxDist,"maxcount":$maxCount,"points":[{"lon":$lonStr,"lat":$latStr}]}"""
    }

    // Checks HTTP status and throws with Frost's error message on non-2xx
    private suspend inline fun <reified T> io.ktor.client.statement.HttpResponse.frostBody(): T {
        if (!status.isSuccess()) throw Exception("Frost ${status.value}: ${bodyAsText()}")
        return body()
    }

    // V0 helpers

    // Finds nearest station IDs via V0 sources endpoint (geography-based)
    // Uses a generous count. small automatic stations may not have monthly aggregates,
    // but with 30 candidates at least one main meteorological station should be included.
    private suspend fun findNearestV0Sources(lat: Double, lon: Double, maxCount: Int = 30): String {
        val lonStr = String.format(java.util.Locale.US, "%.4f", lon)
        val latStr = String.format(java.util.Locale.US, "%.4f", lat)
        val response: FrostV0SourceResponseDto = client.get(FrostRoutes.SOURCES_V0) {
            url.encodedParameters.append("geometry", "nearest(POINT($lonStr%20$latStr))")
            url.encodedParameters.append("nearestmaxcount", maxCount.toString())
            header("Authorization", authHeader)
        }.frostBody()
        if (response.data.isEmpty()) throw Exception("No V0 stations found near ($lat, $lon)")
        return response.data.joinToString(",") { it.id }
    }

    // V0 normals methods

    // Fetches raw monthly temperature observations for 1991-2020 from V0
    // Pre-computed normals (air_temperature_normal P1M 1991_2020) are defined in the Frost catalog
    // but i cant find data in nay endpoints. So we fetch the raw monthly means instead
    // and aggregate them in the repository to produce the 1991-2020 normals
    override suspend fun getTemperatureNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                listOf(
                    // Raw monthly mean temperature 360 entries (12 months × 30 years)
                    "mean(air_temperature P1M)",
                    // Monthly mean of daily maximums
                    "mean(max(air_temperature P1D) P1M)",
                    // Monthly mean of daily minimums
                    "mean(min(air_temperature P1D) P1M)"
                ).joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Fetches raw monthly sunshine hours for 1991-2020 from V0
    // Aggregated in the repository to produce 1991-2020 monthly normals
    override suspend fun getSunshineNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                "sum(duration_of_sunshine%20P1M)"
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Collecting number of days with precipitation >= 1mm per month
    // TODO: frost-rc V1 likely lacks historical aggregates. Likley need V0 migration like temperature
    override suspend fun getPrecipitationNormals(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        return client.get(FrostRoutes.OBSERVATIONS_V1) {
            url.encodedParameters.append("nearest", nearestParam(lat, lon))
            url.encodedParameters.append(
                "elementids",
                listOf("number_of_days_gte(sum(precipitation_amount_normal P1D 1991_2020) P1M 1.0)").joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("time", "1991-01-01T00:00:00Z/2020-12-31T23:59:59Z")
            url.encodedParameters.append("incobs", "true")
            header("Authorization", authHeader)
        }.frostBody()
    }

    override suspend fun getRankedObservationsForParcipitation(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto {
        val response: FrostV1ResponseDto = client.get(FrostV1Routs.RANKED_OBSERVATIONS) {
            parameter("nearest", buildNearest(lat, lon, maxDist,maxCount))
            parameter("time", buildTime(startYear, endYear))
            parameter("elementids", "sum(precipitation_amount P1D)")
            parameter("incobs", true)
            header("Authorization", authHeader)
        }.body()

        return response
    }

    override suspend fun getRankedObservationsForWind(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto {
        val response: FrostV1ResponseDto = client.get(FrostV1Routs.RANKED_OBSERVATIONS){
            parameter("nearest", buildNearest(lat, lon, maxDist,maxCount))
            parameter("time", buildTime(startYear, endYear))
            parameter("elementids", "max(wind_speed_of_gust P1D)")
            parameter("incobs", true)
            header("Authorization", authHeader)
        }.body()

        if(response.data.tseries.isEmpty()){
            //No data for wind_speed_of_gust found, next best thing is wind_speed
            return client.get(FrostV1Routs.RANKED_OBSERVATIONS){
                parameter("nearest", buildNearest(lat, lon, maxDist,maxCount))
                parameter("time", buildTime(startYear, endYear))
                parameter("elementids", "mean(wind_speed P1D)")
                parameter("incobs", true)
                header("Authorization", authHeader)
            }.body()
        } else {
            return response
        }
    }

    // Collecting median precipitation amount (mm) per day per month
    // TODO: rename method, fill in element ID, and likely switch to V0 (frost-rc lacks historical aggregates)
    override suspend fun getPrecipitationMean(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("not yet implemented — element ID unknown, V0 migration likely needed")
    }

    private fun buildNearest(lat: Double, lon: Double, maxdist: Double, maxcount: Int): String {
        val nearest = """{"maxdist":$maxdist,"maxcount":$maxcount,"points":[{"lon":$lon,"lat":$lat}]}"""
        return nearest
    }

    private fun buildTime(startYear: Int, endYear: Int): String {
        val time = "$startYear-01-01T00:00:00Z/$endYear-01-01T00:00:00Z"
        return time
    }
}

    // Fetches raw snow depth history — no pre-computed normal exists, must be aggregated in the repository
    override suspend fun getSnowDepthHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        return client.get(FrostRoutes.OBSERVATIONS_V1) {
            url.encodedParameters.append("nearest", nearestParam(lat, lon, maxCount = 5)) // more stations since snow data is not always available
            url.encodedParameters.append(
                "elementids",
                listOf(
                    // Mean snow depth (cm) per month — raw historical values
                    // No pre-computed normal exists, so we fetch all months 1991-2020
                    // This gives up to 360 data points (12 months × 30 years) that must be aggregated
                    "mean(surface_snow_thickness P1M)",

                    // Highest measured snow depth per month — also raw historical values
                    // Useful for showing snow peaks; may be dropped later
                    "max(surface_snow_thickness P1M)"
                ).joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("time", "1991-01-01T00:00:00Z/2020-12-31T23:59:59Z")
            url.encodedParameters.append("incobs", "true")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // TODO: fill in element ID and likely switch to V0 (frost-rc lacks historical aggregates)
    override suspend fun getWindHistory(
        lat: Double,
        lon: Double
    ): FrostObservationResponseDto {
        TODO("not yet implemented — element ID unknown, V0 migration likely needed")
    }
}