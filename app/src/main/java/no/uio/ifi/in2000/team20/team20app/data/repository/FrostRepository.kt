package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ResponseDto
import no.uio.ifi.in2000.team20.team20app.domain.model.WindAndPrecipitationObservationsResult

/**
 * Interface for fetching processed Frost API climate data.
 *
 * Why:
 * Makes FrostRepository testable by allowing fake implementations in tests.
 */
interface FrostRepositoryService {
    // Each method fetches one climate parameter and wraps in Result to isolate failures.
    // Triple order for temperature: (mean, max, min). For wind: (mean, maxSpeed, maxGust).
    // For sunshine: (hoursPerDay, stationName, distanceKm) includes station metadata. Only 36 stations in Norway that have sunshine data
    suspend fun getTemperatureData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>>
    suspend fun getWindData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>>
    suspend fun getSunshineData(lat: Double, lon: Double): Result<Triple<List<Double>, String?, Double?>>
    suspend fun getSnowData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>>
    suspend fun getPrecipitationData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>>
}

/**
 * FrostRepository
 *
 * Responsibility:
 * - Fetch climate data from FrostDataSource
 * - Map raw DTOs to domain models
 * - Provide stable functions for ViewModels
 *
 * Why:
 * Separates concerns: UI should not care about endpoints or DTO structure.
 */
class FrostRepository(
    private val dataSource: FrostDataSourceService
) : FrostRepositoryService {

    override suspend fun getTemperatureData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> =
        runCatching {
            val data = dataSource.getTemperatureNormals(lat, lon)

            // Log which stations contributed data
            val stationIds = data.data.map { it.sourceId }.distinct()
            Log.d("FrostRepository", "Temperature data from stations: $stationIds (${stationIds.size} stations)")

            // V0 returns 360 raw monthly observations (30 years × 12 months).
            // aggregateByMonthV0 groups by calendar month and averages → 1991-2020 normals computed client-side.
            val meanMap    = data.aggregateByMonthV0("mean(air_temperature P1M)")
            val maxMeanMap = data.aggregateByMonthV0("mean(max(air_temperature P1D) P1M)")
            val minMeanMap = data.aggregateByMonthV0("mean(min(air_temperature P1D) P1M)")

            Triple(
                (1..12).map { meanMap[it]    ?: 0.0 },
                (1..12).map { maxMeanMap[it] ?: 0.0 },
                (1..12).map { minMeanMap[it] ?: 0.0 }
            )
        }

    override suspend fun getWindData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> =
        runCatching {
            val data = dataSource.getWindHistory(lat, lon)

            val windMeanMap = data.aggregateByMonthV0("mean(wind_speed P1M)")
            val windMaxMap  = data.aggregateByMonthV0("max(wind_speed P1M)")
            val windGustMap = data.aggregateByMonthV0("max(wind_speed_of_gust P1M)")

            Triple(
                (1..12).map { windMeanMap[it] ?: 0.0 },
                (1..12).map { windMaxMap[it]  ?: 0.0 },
                (1..12).map { windGustMap[it] ?: 0.0 }
            )
        }

    override suspend fun getSunshineData(lat: Double, lon: Double): Result<Triple<List<Double>, String?, Double?>> =
        runCatching {
            val sunshineResult = dataSource.getSunshineNormals(lat, lon)
            // Aggregated in the repository to produce 1991-2020 monthly normals
            val map = sunshineResult.observations.aggregateByMonthV0("sum(duration_of_sunshine P1M)")
            val daysInMonth = mapOf(
                1 to 31, 2 to 28, 3 to 31, 4 to 30,
                5 to 31, 6 to 30, 7 to 31, 8 to 31,
                9 to 30, 10 to 31, 11 to 30, 12 to 31
            )
            val hoursPerDay = (1..12).map { month ->
                val hours = map[month] ?: 0.0
                hours / (daysInMonth[month] ?: 30)
            }
            Triple(hoursPerDay, sunshineResult.stationName, sunshineResult.distanceKm)
        }

    override suspend fun getSnowData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        // TODO: implement f eks fetch mean(surface_snow_thickness P1M) and max(surface_snow_thickness P1M)
        // from getSnowDepthHistory() in datasource, aggregate with aggregateByMonthV0(),
        // return Pair(meanList, maxList) sorted by month 1–12
        TODO("Not yet implemented")
    }

    override suspend fun getPrecipitationData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> {
        // TODO: implement f eks fetch sum(precipitation_amount_normal P1M 1991_2020) and
        // number_of_days_gte(sum(precipitation_amount_normal P1D 1991_2020) P1M 1.0)
        // from getPrecipitationNormals() in datasource
        // return Pair(amountList, rainyDaysList) sorted by month 1–12
        TODO("Not yet implemented")
    }

    suspend fun getWindAndPrecipitationObservations(
        lat: Double,
        lon: Double
    ): WindAndPrecipitationObservationsResult {
        return coroutineScope {
            val precipitation = async { dataSource.getRankedObservationsForPrecipitation(lat, lon) }
            val wind = async { dataSource.getRankedObservationsForWind(lat, lon) }
            WindAndPrecipitationObservationsResult(
                precipitationValues = precipitation.await().extractValuesWithDate(),
                windValues = wind.await().extractValuesWithDate()
            )
        }
    }

    private fun FrostV1ResponseDto.extractValuesWithDate(): Map<String, Double> =
        data.tseries
            .flatMap { it.observations.orEmpty() }
            .mapNotNull { obs ->
                val value = obs.body.value.toDoubleOrNull() ?: return@mapNotNull null
                if (value <= 0.0) return@mapNotNull null
                obs.time to value
            }
            .toMap()


// ─── V0 mappers ─────────────────────────────────────────────────────────────

    // Groups 30 years of V0 raw monthly observations (360 entries) by calendar month (1–12) and averages.
// Extracts calendar month from ISO 8601 referenceTime (characters 5–6).
// This produces the 1991-2020 monthly normals computed client-side.
    private fun FrostV0ObservationResponseDto.aggregateByMonthV0(elementId: String): Map<Int, Double> {
        return data
            .mapNotNull { entry ->
                val month =
                    entry.referenceTime.substring(5, 7).toIntOrNull() ?: return@mapNotNull null
                val value = entry.observations.find { it.elementId == elementId }?.value
                    ?: return@mapNotNull null
                month to value
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.average() }
    }
}