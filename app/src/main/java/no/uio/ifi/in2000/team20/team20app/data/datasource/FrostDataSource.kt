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
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0AvailableTimeSeriesResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0SourceResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ResponseDto

/**
 * For sunshine data we must do a separate station lookup to find the nearest station
 * because only 36 stations in Norway measure it. This class carries both the raw
 * observations and data about what station was used so the UI can inform the user.
 */
data class SunshineRawResult(
    val stationId: String,
    val stationName: String?,
    val distanceKm: Double?,
    val observations: FrostV0ObservationResponseDto
)

/**
 * Interface for Frost API data fetching.
 *
 * Why:
 * Makes FrostDataSource testable by allowing fake implementations in tests.
 * Temperature normals use V0 (frost.met.no) — historical data not yet in V1.
 * Ranked observation methods use V1 (frost-rc.met.no).
 */
interface FrostDataSourceService {
    suspend fun getTemperatureNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getPrecipitationNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getPrecipitationHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getSnowDepthHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getWindHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto
    suspend fun getSunshineNormals(lat: Double, lon: Double): SunshineRawResult
    suspend fun getRankedObservationsForPrecipitation(lat: Double, lon: Double, startYear: Int = 1980, endYear: Int = 2025, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
    suspend fun getRankedObservationsForWind(lat: Double, lon: Double, startYear: Int = 1980, endYear: Int = 2025, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
}

class FrostDataSource(
    private val client: HttpClient,
    private val credentials: String // format: "clientId:clientSecret"
) : FrostDataSourceService {

    private val authHeader: String
        get() {
            val header = "Basic " + credentials.encodeBase64()
            return header
        }

    // Checks HTTP status and throws with Frost's error body on non-2xx responses
    private suspend inline fun <reified T> io.ktor.client.statement.HttpResponse.frostBody(): T {
        if (!status.isSuccess()) throw Exception("Frost ${status.value}: ${bodyAsText()}")
        return body()
    }

    // V0 helpers

    // Finds nearest station IDs via V0 sources endpoint (geography-based)
    // Uses a generous count. small automatic stations may not have monthly aggregates,
    // but with 30 candidates at least one main meteorological station should be included.
    // TODO: create fallback if no stations are found? look into this
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

    // Fetches raw monthly temperature observations for 1991-2020 from V0
    // Pre-computed normals (air_temperature_normal P1M 1991_2020) are defined in the Frost catalog
    // but are not available in any endpoints. So we fetch the raw monthly means instead
    // and aggregate them in the repository to produce the 1991-2020 normals
    override suspend fun getTemperatureNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                listOf(
                    // Raw monthly mean temperature 360 entries (12 months * 30 years)
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

    // Fetches raw monthly rainy days (>= 1mm). raw values 1991-2020 that must be aggregated client-side
    override suspend fun getPrecipitationNormals(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                "number_of_days_gte(sum(precipitation_amount%20P1D)%20P1M%201.0)"
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Fetches raw monthly max daily precipitation. no pre-computed normal exists, must be aggregated in the repository
    override suspend fun getPrecipitationHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                "max(sum(precipitation_amount%20P1D)%20P1M)"
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Fetches raw snow depth history — no pre-computed normal exists, must be aggregated in the repository
    override suspend fun getSnowDepthHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                listOf(
                    "mean(surface_snow_thickness P1M)",
                    "max(surface_snow_thickness P1M)"
                ).joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Fetches raw wind history — no pre-computed normal exists, must be aggregated in the repository
    override suspend fun getWindHistory(lat: Double, lon: Double): FrostV0ObservationResponseDto {
        val sources = findNearestV0Sources(lat, lon)
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                listOf(
                    "mean(wind_speed P1M)",
                    "max(wind_speed P1M)",
                    "max(wind_speed_of_gust P1M)"
                ).joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Finds the nearest station that has sunshine data for the 1991-2020 period
    // Returns stationId, stationName, and distance in km
    private suspend fun findNearestSunshineStation(lat: Double, lon: Double): Triple<String, String?, Double?> {
        try {
            val availableResponse: FrostV0AvailableTimeSeriesResponseDto = client.get(FrostRoutes.AVAILABLE_TIMESERIES_V0) {
                url.encodedParameters.append("elements", "sum(duration_of_sunshine%20P1M)")
                url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
                url.encodedParameters.append("fields", "sourceId")
                header("Authorization", authHeader)
            }.let { response ->
                if (!response.status.isSuccess()) {
                    Log.e("FrostDataSource", "availableTimeSeries error ${response.status.value}: ${response.bodyAsText()}")
                    throw Exception("Frost ${response.status.value}: ${response.bodyAsText()}")
                }
                response.body()
            }

            if (availableResponse.data.isEmpty()) {
                throw Exception("No stations found with sunshine data for 1991-2020")
            }

            val allStationIds = availableResponse.data
                .map { it.sourceId.removeSuffix(":0") }
                .distinct()
            val stationIds = allStationIds.take(50).joinToString(",")

            val lonStr = String.format(java.util.Locale.US, "%.4f", lon)
            val latStr = String.format(java.util.Locale.US, "%.4f", lat)
            val sourcesResponse: FrostV0SourceResponseDto = client.get(FrostRoutes.SOURCES_V0) {
                url.encodedParameters.append("geometry", "nearest(POINT($lonStr%20$latStr))")
                url.encodedParameters.append("nearestmaxcount", "200")
                url.encodedParameters.append("ids", stationIds)
                header("Authorization", authHeader)
            }.frostBody()

            if (sourcesResponse.data.isEmpty()) {
                throw Exception("No nearby stations with sunshine data found")
            }

            val nearestStation = sourcesResponse.data.first()
            return Triple(nearestStation.id, nearestStation.name, nearestStation.distance)
        } catch (e: Exception) {
            throw e
        }
    }

    // Fetches raw monthly sunshine hours for 1991-2020 from V0
    // Finds the nearest station with actual sunshine data to avoid HTTP 412 failures
    override suspend fun getSunshineNormals(lat: Double, lon: Double): SunshineRawResult {
        val (stationId, stationName, distance) = findNearestSunshineStation(lat, lon)
        val observations: FrostV0ObservationResponseDto = client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", stationId)
            url.encodedParameters.append(
                "elements",
                listOf("sum(duration_of_sunshine P1M)").joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()

        return SunshineRawResult(
            stationId = stationId,
            stationName = stationName,
            distanceKm = distance,
            observations = observations
        )
    }

    // V1 ranked observation methods (used by GeoScore algorithm)
    // Includes fallback to wider search radius if no stations found nearby

    override suspend fun getRankedObservationsForPrecipitation(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto {
        for (dist in listOf(10.0, 20.0, 30.0)) {
            val response: FrostV1ResponseDto = client.get(FrostRoutes.OBSERVATIONS_V1) {
                parameter("nearest", buildNearest(lat, lon, dist, maxCount))
                parameter("time", buildTime(startYear, endYear))
                parameter("elementids", "sum(precipitation_amount P1D)")
                parameter("incobs", true)
                header("Authorization", authHeader)
            }.body()
            if (response.data.tseries.isNotEmpty()) return response
        }
        return FrostV1ResponseDto()
    }

    override suspend fun getRankedObservationsForWind(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto {
        val windElementIds = listOf("max(wind_speed_of_gust P1D)", "mean(wind_speed P1D)")
        for (dist in listOf(10.0, 20.0, 30.0)) {
            for (elementId in windElementIds) {
                val response: FrostV1ResponseDto = client.get(FrostRoutes.OBSERVATIONS_V1) {
                    parameter("nearest", buildNearest(lat, lon, dist, maxCount))
                    parameter("time", buildTime(startYear, endYear))
                    parameter("elementids", elementId)
                    parameter("incobs", true)
                    header("Authorization", authHeader)
                }.body()
                if (response.data.tseries.isNotEmpty()) return response
            }
        }
        return FrostV1ResponseDto()
    }

    private fun buildNearest(lat: Double, lon: Double, maxDist: Double = 10.0, maxCount: Int = 5): String {
        return """{"maxdist":$maxDist,"maxcount":$maxCount,"points":[{"lon":$lon,"lat":$lat}]}"""
    }

    private fun buildTime(startYear: Int, endYear: Int): String =
        "$startYear-01-01T00:00:00Z/$endYear-01-01T00:00:00Z"
}
