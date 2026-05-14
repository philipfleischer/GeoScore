package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV0ObservationResponseDto
import no.uio.ifi.in2000.team20.team20app.data.dto.FrostV1ResponseDto
import no.uio.ifi.in2000.team20.team20app.data.local.dao.PrecipitationCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.dao.SnowCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.dao.SunshineCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.dao.TemperatureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.dao.WindCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.entity.PrecipitationCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.entity.SnowCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.entity.SunshineCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.entity.TemperatureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.entity.WindCacheEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.WindAndPrecipitationObservationsResult
import javax.inject.Inject

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
    suspend fun getWindAndPrecipitationObservations(lat: Double, lon: Double): WindAndPrecipitationObservationsResult
}

/**
 * FrostRepository
 *
 * Responsibility:
 * - Fetch climate data from FrostDataSource (or Room cache if available)
 * - Map raw DTOs to domain models
 * - Provide stable functions for ViewModels
 *
 * Caching strategy:
 * - Each of the 5 climate data types has its own Room cache table.
 * - Cache is keyed by location rounded to 2 decimal places (~1km precision).
 * - Historical normals (1991–2020) never change, so cache entries never expire.
 * - On cache hit: return stored data immediately, no network call.
 * - On cache miss: fetch from API, write to cache, return result.
 *
 * Why:
 * Separates concerns: UI should not care about endpoints or DTO structure.
 */
class FrostRepository @Inject constructor(
    private val dataSource: FrostDataSourceService,
    private val temperatureCacheDao: TemperatureCacheDao,
    private val windCacheDao: WindCacheDao,
    private val sunshineCacheDao: SunshineCacheDao,
    private val snowCacheDao: SnowCacheDao,
    private val precipitationCacheDao: PrecipitationCacheDao
) : FrostRepositoryService {

    // In-memory cache for station IDs keyed by location (rounded to 2 decimal places)
    // Prevents redundant API calls when fetching multiple climate parameters for the same location
    private val stationCache = mutableMapOf<String, String>()

    // In-memory cache for sunshine station IDs keyed by location (rounded to 2 decimal places)
    // Prevents redundant API calls when searching for nearest sunshine station
    private val sunshineStationCache = mutableMapOf<String, String>()

    override suspend fun getTemperatureData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> =
        runCatching {
            // Fetch stations once, then data
            val stations = getOrCacheStations(lat, lon)
            val stationId = stations.split(",").first()  // Use nearest (first) station as cache key

            // Cache hit: return stored normals without a network call
            val cached = temperatureCacheDao.getByKey(stationId)
            if (cached != null) {
                return@runCatching Triple(
                    fromJson(cached.monthlyMean),
                    fromJson(cached.monthlyMax),
                    fromJson(cached.monthlyMin)
                )
            }

            // Cache miss: fetch from API
            val data = dataSource.getTemperatureNormals(lat, lon, stations)

            // Log which stations contributed data
            val stationIds = data.data.map { it.sourceId }.distinct()
            Log.d("FrostRepository", "Temperature data from stations: $stationIds (${stationIds.size} stations)")

            // V0 returns 360 raw monthly observations (30 years × 12 months).
            // aggregateByMonthV0 groups by calendar month and averages → 1991-2020 normals computed client-side.
            val meanMap    = data.aggregateByMonthV0("mean(air_temperature P1M)")
            val maxMeanMap = data.aggregateByMonthV0("mean(max(air_temperature P1D) P1M)")
            val minMeanMap = data.aggregateByMonthV0("mean(min(air_temperature P1D) P1M)")

            val meanList = (1..12).map { meanMap[it]    ?: 0.0 }
            val maxList  = (1..12).map { maxMeanMap[it] ?: 0.0 }
            val minList  = (1..12).map { minMeanMap[it] ?: 0.0 }

            temperatureCacheDao.insert(
                TemperatureCacheEntity(
                    stationId = stationId,
                    monthlyMean = toJson(meanList),
                    monthlyMax  = toJson(maxList),
                    monthlyMin  = toJson(minList)
                )
            )
            Log.d("FrostRepository", "Temperature data cached for stationId: $stationId")

            Triple(meanList, maxList, minList)
        }

    override suspend fun getWindData(lat: Double, lon: Double): Result<Triple<List<Double>, List<Double>, List<Double>>> =
        runCatching {
            val stations = getOrCacheStations(lat, lon)
            val stationId = stations.split(",").first()  // Use nearest (first) station as cache key

            val cached = windCacheDao.getByKey(stationId)
            if (cached != null) {
                return@runCatching Triple(
                    fromJson(cached.monthlyMean),
                    fromJson(cached.monthlyMaxSpeed),
                    fromJson(cached.monthlyMaxGust)
                )
            }

            val data = dataSource.getWindHistory(lat, lon, stations)

            val windMeanMap = data.aggregateByMonthV0("mean(wind_speed P1M)")
            val windMaxMap  = data.aggregateByMonthV0("max(wind_speed P1M)")
            val windGustMap = data.aggregateByMonthV0("max(wind_speed_of_gust P1M)")

            val meanList  = (1..12).map { windMeanMap[it] ?: 0.0 }
            val maxList   = (1..12).map { windMaxMap[it]  ?: 0.0 }
            val gustList  = (1..12).map { windGustMap[it] ?: 0.0 }

            windCacheDao.insert(
                WindCacheEntity(
                    stationId    = stationId,
                    monthlyMean    = toJson(meanList),
                    monthlyMaxSpeed = toJson(maxList),
                    monthlyMaxGust = toJson(gustList)
                )
            )
            Log.d("FrostRepository", "Wind data cached for stationId: $stationId")

            Triple(meanList, maxList, gustList)
        }

     override suspend fun getSunshineData(lat: Double, lon: Double): Result<Triple<List<Double>, String?, Double?>> =
         runCatching {
             // Get sunshine station (uses in-memory cache)
             val stationId = getOrCacheSunshineStation(lat, lon)

             val cached = sunshineCacheDao.getByKey(stationId)
             if (cached != null) {
                 return@runCatching Triple(
                     fromJson(cached.monthlyHoursPerDay),
                     cached.stationName,
                     cached.distanceKm
                 )
             }

             val sunshineResult = dataSource.getSunshineNormals(lat, lon, stationId)
             
             // Aggregated in the repository to produce 1991-2020 monthly normals
             val map = sunshineResult.observations.aggregateByMonthV0("sum(duration_of_sunshine P1M)")
             val daysInMonth = mapOf(
                 1 to 31, 2 to 28, 3 to 31, 4 to 30,
                 5 to 31, 6 to 30, 7 to 31, 8 to 31,
                 9 to 30, 10 to 31, 11 to 30, 12 to 31
             )
             val hoursPerDay = (1..12).map { month ->
                 val hours = map[month] ?: 0.0
                 maxOf(0.0, hours / (daysInMonth[month] ?: 30))
             }

             sunshineCacheDao.insert(
                 SunshineCacheEntity(
                     stationId        = stationId,
                     monthlyHoursPerDay = toJson(hoursPerDay),
                     stationName        = sunshineResult.stationName,
                     distanceKm         = sunshineResult.distanceKm
                 )
             )
             Log.d("FrostRepository", "Sunshine data cached for stationId: $stationId")

             Triple(hoursPerDay, sunshineResult.stationName, sunshineResult.distanceKm)
         }

    override suspend fun getSnowData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> =
        runCatching {
            val stations = getOrCacheStations(lat, lon)
            val stationId = stations.split(",").first()  // Use nearest (first) station as cache key

            val cached = snowCacheDao.getByKey(stationId)
            if (cached != null) {
                return@runCatching Pair(
                    fromJson(cached.monthlyMean),
                    fromJson(cached.monthlyMax)
                )
            }

            val raw = dataSource.getSnowDepthHistory(lat, lon, stations)

            val meanMap = raw.aggregateByMonthV0("mean(surface_snow_thickness P1M)")
                .mapValues { if (it.value < 0) 0.0 else it.value }
            val maxMap = raw.aggregateByMonthV0("max(surface_snow_thickness P1M)")
                .mapValues { if (it.value < 0) 0.0 else it.value }

            val meanList = (1..12).map { meanMap[it] ?: 0.0 }
            val maxList  = (1..12).map { maxMap[it] ?: 0.0 }

            snowCacheDao.insert(
                SnowCacheEntity(
                    stationId = stationId,
                    monthlyMean = toJson(meanList),
                    monthlyMax  = toJson(maxList)
                )
            )
            Log.d("FrostRepository", "Snow data cached for stationId: $stationId")

            Pair(meanList, maxList)
        }

    override suspend fun getPrecipitationData(lat: Double, lon: Double): Result<Pair<List<Double>, List<Double>>> =
        runCatching {
            val stations = getOrCacheStations(lat, lon)
            val stationId = stations.split(",").first()  // Use nearest (first) station as cache key

            val cached = precipitationCacheDao.getByKey(stationId)
            if (cached != null) {
                return@runCatching Pair(
                    fromJson(cached.monthlyRainyDays),
                    fromJson(cached.monthlyMaxDaily)
                )
            }

            // Rainy days. raw aggregation since pre-computed normal is unavailable
            val rainyDaysRaw = dataSource.getPrecipitationNormals(lat, lon, stations)
            val rainyDays = rainyDaysRaw.aggregateByMonthV0(
                "number_of_days_gte(sum(precipitation_amount P1D) P1M 1.0)"
            )

            // Max daily precipitation. no normal exists, always raw
            val maxDaily = dataSource.getPrecipitationHistory(lat, lon, stations)
                .aggregateByMonthV0("max(sum(precipitation_amount P1D) P1M)")

            val rainyDaysList = (1..12).map { rainyDays[it] ?: 0.0 }
            val maxDailyList  = (1..12).map { maxDaily[it] ?: 0.0 }

            precipitationCacheDao.insert(
                PrecipitationCacheEntity(
                    stationId      = stationId,
                    monthlyRainyDays = toJson(rainyDaysList),
                    monthlyMaxDaily  = toJson(maxDailyList)
                )
            )
            Log.d("FrostRepository", "Precipitation data cached for stationId: $stationId")

            Pair(rainyDaysList, maxDailyList)
        }

    override suspend fun getWindAndPrecipitationObservations(
        lat: Double,
        lon: Double
    ): WindAndPrecipitationObservationsResult {
        return coroutineScope {
            Log.d("FrostRepository", "Wind data getting fetched")
            val precipitation = async { dataSource.getRankedObservationsForPrecipitation(lat, lon) }
            val wind = async { dataSource.getRankedObservationsForWind(lat, lon)
            }
            WindAndPrecipitationObservationsResult(
                precipitationValues = precipitation.await().extractValuesWithDate(),
                windValues = wind.await().extractValuesWithDate()
            )
        }
    }

    // Fetches and caches the 30 nearest station IDs for a location
    // Uses the same location key format as the data methods to ensure cache reuse
    private suspend fun getOrCacheStations(lat: Double, lon: Double): String {
        val key = formatLocationKey(lat, lon)
        return stationCache[key] ?: run {
            val stations = dataSource.getStationsNearby(lat, lon)
            stationCache[key] = stations
            Log.d("FrostRepository", "Fetched stations for $key: $stations")
            stations
        }
    }

    // Fetches and caches the nearest sunshine station ID for a location
    // Uses the same location key format to ensure cache reuse across requests
    private suspend fun getOrCacheSunshineStation(lat: Double, lon: Double): String {
        val key = formatLocationKey(lat, lon)
        return sunshineStationCache[key] ?: run {
            val sunshineStationId = dataSource.getSunshineStationNearby(lat, lon)
            sunshineStationCache[key] = sunshineStationId
            Log.d("FrostRepository", "Fetched sunshine station for $key: $sunshineStationId")
            sunshineStationId
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


// ─── Helpers ────────────────────────────────────────────────────────────────

    // Rounds lat/lon to 2 decimal places (~1km precision) to form a stable cache key.
    // Matches the format used by GeoScore use cases: "%.2f, %.2f".format(lat, lon)
    private fun formatLocationKey(lat: Double, lon: Double): String =
        "%.2f, %.2f".format(lat, lon)

    // Serializes a list of monthly values to a JSON string for storage in Room.
    private fun toJson(values: List<Double>): String =
        Json.encodeToString(values)

    // Deserializes a JSON string from Room back into a list of monthly values.
    private fun fromJson(json: String): List<Double> =
        Json.decodeFromString(json)


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
