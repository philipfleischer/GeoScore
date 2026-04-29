package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostStats
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostTemperatureNormal
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostWindNormal
import no.uio.ifi.in2000.team20.team20app.data.model.FrostV1ResponseDto
import no.uio.ifi.in2000.team20.team20app.domain.model.WindAndPrecipitationObservationsResult

/**
 * Interface for fetching processed Frost API climate data.
 *
 * Why:
 * Makes FrostRepository testable by allowing fake implementations in tests.
 */
interface FrostRepositoryService {
    // Fetches all climate parameters in parallel and returns a single domain model
    suspend fun getFrostStats(lat: Double, lon: Double): FrostStats
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

    // Launches all datasource calls concurrently with async, then maps and combines into FrostStats.
    override suspend fun getFrostStats(lat: Double, lon: Double): FrostStats =
        coroutineScope {
            val tempDeferred = async { dataSource.getTemperatureNormals(lat, lon) }
            val sunDeferred = async { dataSource.getSunshineNormals(lat, lon) }
            val windDeferred = async { dataSource.getWindHistory(lat, lon) }
            // TODO: add remaining async calls once other datasource methods are implemented
            // val precipDeferred  = async { dataSource.getPrecipitationNormals(lat, lon) }
            // val snowDeferred    = async { dataSource.getSnowDepthHistory(lat, lon) }

            val tempData = tempDeferred.await()
            val sunData = sunDeferred.await()
            val windData = windDeferred.await()

            // Log which stations contributed data
            val stationIds = tempData.data.map { it.sourceId }.distinct()
            Log.d(
                "FrostRepository",
                "Temperature data from stations: $stationIds (${stationIds.size} stations)"
            )

            // V0 returns 360 raw monthly observations (30 years × 12 months).
            // aggregateByMonthV0 groups by calendar month and averages → 1991-2020 normals computed client-side.
            val meanMap = tempData.aggregateByMonthV0("mean(air_temperature P1M)")
            val maxMeanMap = tempData.aggregateByMonthV0("mean(max(air_temperature P1D) P1M)")
            val minMeanMap = tempData.aggregateByMonthV0("mean(min(air_temperature P1D) P1M)")

            val temperatureNormals = (1..12).associateWith { month ->
                FrostTemperatureNormal(
                    mean = meanMap[month],
                    maxMean = maxMeanMap[month],
                    minMean = minMeanMap[month]
                )
            }

            val sunshineNormals = sunData.aggregateByMonthV0("sum(duration_of_sunshine P1M)")
            val windMeanMap     = windData.aggregateByMonthV0("mean(wind_speed P1M)")
            val windMaxMap      = windData.aggregateByMonthV0("max(wind_speed P1M)")
            val windGustMap     = windData.aggregateByMonthV0("max(wind_speed_of_gust P1M)")

            val windNormals = (1..12).associateWith { month ->
                FrostWindNormal(
                    mean     = windMeanMap[month],
                    maxSpeed = windMaxMap[month],
                    maxGust  = windGustMap[month]
                )
            }.ifEmpty { null }

            FrostStats(
                temperatureNormals = temperatureNormals,
                precipitation = null,        // TODO: implement
                snowDepth = null,            // TODO: implement
                wind = windNormals,
                sunshineNormals = sunshineNormals.ifEmpty { null }
            )
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

// ─── V1 mappers ─────────────────────────────────────────────────────────────

    // Finds the first V1 time series matching elementId and maps observations to month (1–12) -> value.
    private fun FrostObservationResponseDto.toMonthlyMap(elementId: String): Map<Int, Double> {
        return tseries
            .firstOrNull { it.header.extra?.element?.id == elementId }
            ?.observations
            ?.mapNotNull { obs ->
                val month = obs.time.substring(5, 7).toIntOrNull() ?: return@mapNotNull null
                val value = obs.body.value?.toDoubleOrNull() ?: return@mapNotNull null
                month to value
            }
            ?.toMap()
            ?: emptyMap()
    }

    // Groups raw 30-year V1 monthly observations by calendar month (1–12) and averages the values.
// Used for parameters without a pre-computed normal (snow depth, wind, precipitation).
    private fun FrostObservationResponseDto.aggregateByMonth(elementId: String): Map<Int, Double> {
        val observations = tseries
            .firstOrNull { it.header.extra?.element?.id == elementId }
            ?.observations ?: return emptyMap()

        return observations
            .mapNotNull { obs ->
                val month = obs.time.substring(5, 7).toIntOrNull() ?: return@mapNotNull null
                val value = obs.body.value?.toDoubleOrNull() ?: return@mapNotNull null
                month to value
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.average() }
    }
}