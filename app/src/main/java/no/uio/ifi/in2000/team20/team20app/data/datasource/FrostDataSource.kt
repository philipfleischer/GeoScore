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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team20.team20app.data.api.FrostRoutes
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV0AvailableTimeSeriesResponseDto
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV0SourceResponseDto
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV1ResponseDto
import no.uio.ifi.in2000.team20.team20app.di.FrostClient
import no.uio.ifi.in2000.team20.team20app.util.Constants
import javax.inject.Inject

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
    suspend fun getStationsNearby(lat: Double, lon: Double): String
    suspend fun getSunshineStationNearby(lat: Double, lon: Double): String
    suspend fun getTemperatureNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    suspend fun getPrecipitationNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    suspend fun getPrecipitationHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    suspend fun getSnowDepthHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    suspend fun getWindHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    suspend fun getSunshineNormals(lat: Double, lon: Double, stationId: String): SunshineRawResult
    suspend fun getRankedObservationsForPrecipitation(lat: Double, lon: Double, startYear: Int = 1990, endYear: Int = 2020, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
    suspend fun getRankedObservationsForWind(lat: Double, lon: Double, startYear: Int = 1990, endYear: Int = 2020, maxDist: Double = 10.0, maxCount: Int = 5): FrostV1ResponseDto
}

class FrostDataSource @Inject constructor(
    @param:FrostClient private val client: HttpClient
) : FrostDataSourceService {

    private val authHeader: String
        get() = "Basic " + "${Constants.FROST_CLIENT_ID}:${Constants.FROST_CLIENT_SECRET}".encodeBase64() //Consider migrating

    // Checks HTTP status and throws with Frost's error body on non-2xx responses
    private suspend inline fun <reified T> io.ktor.client.statement.HttpResponse.frostBody(): T {
        if (!status.isSuccess()) throw Exception("Frost ${status.value}: ${bodyAsText()}")
        return body()
    }

    // Fetches the 10 nearest station IDs for a given location
    override suspend fun getStationsNearby(lat: Double, lon: Double): String {
        return findNearestV0Sources(lat, lon)
    }

    // Fetches the nearest station that has sunshine data for the 1991-2020 period
    override suspend fun getSunshineStationNearby(lat: Double, lon: Double): String {
        return findNearestSunshineStationId(lat, lon)
    }

    // V0 helpers

    // Finds nearest station IDs via V0 sources endpoint (geography-based)
    // Requests 10 nearest stations to aggregate climate normals client-side (1991-2020).
    // All 10 stations contribute to monthly averages for temperature, wind, snow, and precipitation,
    // providing robust normals less sensitive to data gaps at individual stations.
    private suspend fun findNearestV0Sources(lat: Double, lon: Double, maxCount: Int = 10): String {
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
    override suspend fun getTemperatureNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto {
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
    override suspend fun getPrecipitationNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto {
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
    override suspend fun getPrecipitationHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto {
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
    override suspend fun getSnowDepthHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto {
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
    override suspend fun getWindHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto {
        return client.get(FrostRoutes.OBSERVATIONS_V0) {
            url.encodedParameters.append("sources", sources)
            url.encodedParameters.append(
                "elements",
                listOf(
                    // Mean wind speed per month - raw historical values
                    // "This is how the wind typically blows here in January"
                    "mean(wind_speed P1M)",
                    // Highest measured wind speed per month - raw
                    // "This is how strong the average wind can get in January, typically storm force in January"
                    "max(wind_speed P1M)",
                    // Highest measured wind gust per month - raw
                    // "This is how strong the wind gusts can be in January"
                    "max(wind_speed_of_gust P1M)"
                ).joinToString(",").replace(" ", "%20")
            )
            url.encodedParameters.append("referencetime", "1991-01-01/2020-12-31")
            header("Authorization", authHeader)
        }.frostBody()
    }

    // Finds the nearest station that has sunshine data for the 1991-2020 period
    // Returns just the station ID string (station details handled by Repository)
    private suspend fun findNearestSunshineStationId(lat: Double, lon: Double): String {
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

        val allSunshineIds = availableResponse.data
            .map { it.sourceId.removeSuffix(":0") }
            .distinct()
            .joinToString(",")

        val lonStr = String.format(java.util.Locale.US, "%.4f", lon)
        val latStr = String.format(java.util.Locale.US, "%.4f", lat)
        val sourcesResponse: FrostV0SourceResponseDto = client.get(FrostRoutes.SOURCES_V0) {
            url.encodedParameters.append("geometry", "nearest(POINT($lonStr%20$latStr))")
            url.encodedParameters.append("ids", allSunshineIds)
            url.encodedParameters.append("nearestmaxcount", "1")
            header("Authorization", authHeader)
        }.frostBody()

        if (sourcesResponse.data.isEmpty()) {
            throw Exception("No nearby stations with sunshine data found")
        }

        return sourcesResponse.data.first().id
    }

     // Fetches raw monthly sunshine hours for 1991-2020 from V0
     // Uses the provided stationId (fetched and cached by Repository)
     // Also fetches station metadata (name, distance) to display where the data came from
     override suspend fun getSunshineNormals(lat: Double, lon: Double, stationId: String): SunshineRawResult {
         // Fetch station metadata to get name and distance. Frost expects lat and lon wiith 4 decimal places
         val lonStr = String.format(java.util.Locale.US, "%.4f", lon)
         val latStr = String.format(java.util.Locale.US, "%.4f", lat)

         val stationMetadata: FrostV0SourceResponseDto = client.get(FrostRoutes.SOURCES_V0) {
             url.encodedParameters.append("geometry", "nearest(POINT($lonStr%20$latStr))")
             url.encodedParameters.append("ids", stationId)
             header("Authorization", authHeader)
         }.frostBody()

         val station = stationMetadata.data.firstOrNull()

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
             stationName = station?.name,
             distanceKm = station?.distance,
             observations = observations
         )
     }

    // V1 ranked observation methods (used by GeoScore algorithm)
    // Radii (10, 20, 30 km) are tried in parallel to avoid sequential fallback latency

    override suspend fun getRankedObservationsForPrecipitation(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto = coroutineScope {
        val d10 = async { fetchV1(lat, lon, 10.0, maxCount, startYear, endYear, "sum(precipitation_amount P1D)") }
        val d20 = async { fetchV1(lat, lon, 20.0, maxCount, startYear, endYear, "sum(precipitation_amount P1D)") }
        val d30 = async { fetchV1(lat, lon, 30.0, maxCount, startYear, endYear, "sum(precipitation_amount P1D)") }

        val (r10, r20, r30) = awaitAll(d10, d20, d30)
        Log.d("FrostDataSource", "Precip 10km: ${r10.data.tseries.size} stns – ${r10.data.tseries.isNotEmpty()}")
        Log.d("FrostDataSource", "Precip 20km: ${r20.data.tseries.size} stns – ${r20.data.tseries.isNotEmpty()}")
        Log.d("FrostDataSource", "Precip 30km: ${r30.data.tseries.size} stns – ${r30.data.tseries.isNotEmpty()}")

        when {
            r10.data.tseries.isNotEmpty() -> r10
            r20.data.tseries.isNotEmpty() -> r20
            r30.data.tseries.isNotEmpty() -> r30
            else -> FrostV1ResponseDto()
        }
    }

    override suspend fun getRankedObservationsForWind(
        lat: Double,
        lon: Double,
        startYear: Int,
        endYear: Int,
        maxDist: Double,
        maxCount: Int
    ): FrostV1ResponseDto {
        // Round 1: wind gust at all radiuses in parallel
        val gustResult = coroutineScope {
            val d10 = async { fetchV1(lat, lon, 10.0, maxCount, startYear, endYear, "max(wind_speed_of_gust P1D)") }
            val d20 = async { fetchV1(lat, lon, 20.0, maxCount, startYear, endYear, "max(wind_speed_of_gust P1D)") }
            val d30 = async { fetchV1(lat, lon, 30.0, maxCount, startYear, endYear, "max(wind_speed_of_gust P1D)") }

            val (r10, r20, r30) = awaitAll(d10, d20, d30)
            Log.d("FrostDataSource", "WindGust 10km: ${r10.data.tseries.size} stns – ${r10.data.tseries.isNotEmpty()}")
            Log.d("FrostDataSource", "WindGust 20km: ${r20.data.tseries.size} stns – ${r20.data.tseries.isNotEmpty()}")
            Log.d("FrostDataSource", "WindGust 30km: ${r30.data.tseries.size} stns – ${r30.data.tseries.isNotEmpty()}")

            //Choose data for the closest one that is not empty
            when {
                r10.data.tseries.isNotEmpty() -> r10
                r20.data.tseries.isNotEmpty() -> r20
                r30.data.tseries.isNotEmpty() -> r30
                else -> null
            }
        }
        if (gustResult != null) return gustResult

        // Round 2: fallback to mean wind speed at all radiuses in parallel
        return coroutineScope {
            val d10 = async { fetchV1(lat, lon, 10.0, maxCount, startYear, endYear, "mean(wind_speed P1D)") }
            val d20 = async { fetchV1(lat, lon, 20.0, maxCount, startYear, endYear, "mean(wind_speed P1D)") }
            val d30 = async { fetchV1(lat, lon, 30.0, maxCount, startYear, endYear, "mean(wind_speed P1D)") }

            val (r10, r20, r30) = awaitAll(d10, d20, d30)
            Log.d("FrostDataSource", "WindMean 10km: ${r10.data.tseries.size} stns – ${r10.data.tseries.isNotEmpty()}")
            Log.d("FrostDataSource", "WindMean 20km: ${r20.data.tseries.size} stns – ${r20.data.tseries.isNotEmpty()}")
            Log.d("FrostDataSource", "WindMean 30km: ${r30.data.tseries.size} stns – ${r30.data.tseries.isNotEmpty()}")

            //Choose data for the closest one that is not empty
            when {
                r10.data.tseries.isNotEmpty() -> r10
                r20.data.tseries.isNotEmpty() -> r20
                r30.data.tseries.isNotEmpty() -> r30
                else -> FrostV1ResponseDto()
            }
        }
    }

    private suspend fun fetchV1(
        lat: Double,
        lon: Double,
        dist: Double,
        maxCount: Int,
        startYear: Int,
        endYear: Int,
        elementId: String
    ): FrostV1ResponseDto {
        val response = client.get(FrostRoutes.OBSERVATIONS_V1) {
            parameter("nearest", buildNearest(lat, lon, dist, maxCount))
            parameter("time", buildTime(startYear, endYear))
            parameter("elementids", elementId)
            parameter("incobs", true)
            header("Authorization", authHeader)
        }
        Log.d("FrostDataSource", "fetchV1 [${elementId}] dist=${dist}km → HTTP ${response.status.value}")
        if (!response.status.isSuccess()) {
            Log.e("FrostDataSource", "fetchV1 error body: ${response.bodyAsText()}")
            return FrostV1ResponseDto()
        }
        return response.body()
    }

    private fun buildNearest(lat: Double, lon: Double, maxDist: Double = 10.0, maxCount: Int = 2): String {
        return """{"maxdist":$maxDist,"maxcount":$maxCount,"points":[{"lon":$lon,"lat":$lat}]}"""
    }

    private fun buildTime(startYear: Int, endYear: Int): String =
        "$startYear-01-01T00:00:00Z/$endYear-01-01T00:00:00Z"
}
