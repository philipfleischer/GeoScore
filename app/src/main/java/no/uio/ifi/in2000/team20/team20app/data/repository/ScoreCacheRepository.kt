package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ExposureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.HazardCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.VulnerabilityCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.HazardCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TotalScoreCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.VulnerabilityCacheEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.ExposureScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.VulnerabilityScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import javax.inject.Inject

/**
 * Repository responsible for caching computed geo-scores.
 *
 * Responsibility:
 * - Provide a single data-layer entry point for reading and writing all
 *   score-related cache tables (hazard, exposure, vulnerability, total).
 * - Accept domain model types on save methods and map to Room entities
 *   internally, so the domain layer has no knowledge of Room.
 *
 * Why:
 * Keeps Room DAO access and entity construction in the data layer,
 * decoupling use cases from both DAOs and @Entity classes.
 */
interface ScoreCacheRepository {
    // Hazard
    suspend fun getHazardCache(locationKey: String): HazardScoreResult?
    suspend fun saveHazardScore(locationKey: String, result: HazardScoreResult)

    // Exposure
    suspend fun getExposureCache(locationKey: String): ExposureScoreResult?
    suspend fun saveExposureScore(locationKey: String, result: ExposureScoreResult)

    // Vulnerability
    suspend fun getVulnerabilityCache(locationKey: String): VulnerabilityScoreResult?
    suspend fun saveVulnerabilityScore(
        locationKey: String,
        result: VulnerabilityScoreResult,
        isInFloodZone: Boolean,
        isInLandslideZone: Boolean
    )

    // Total / composite GeoScore
    suspend fun getGeoScoreCache(locationKey: String): GeoScore?
    suspend fun saveGeoScore(geoScore: GeoScore)
}

class ScoreCacheRepositoryImpl @Inject constructor(
    private val hazardCacheDao: HazardCacheDao,
    private val exposureCacheDao: ExposureCacheDao,
    private val vulnerabilityCacheDao: VulnerabilityCacheDao,
    private val totalScoreCacheDao: TotalScoreCacheDao
) : ScoreCacheRepository {

    override suspend fun getHazardCache(locationKey: String): HazardScoreResult? {
        val entity = hazardCacheDao.getByKey(locationKey) ?: return null
        return HazardScoreResult(
            precipitationScore = entity.precipitationScore,
            windScore = entity.windScore,
            hazardScore = entity.score
        )
    }

    override suspend fun saveHazardScore(locationKey: String, result: HazardScoreResult) {
        hazardCacheDao.insert(
            HazardCacheEntity(
                locationKey = locationKey,
                precipitationScore = result.precipitationScore,
                windScore = result.windScore,
                score = result.hazardScore
            )
        )
    }

    override suspend fun getExposureCache(locationKey: String): ExposureScoreResult? {
        val entity = exposureCacheDao.getByKey(locationKey) ?: return null
        return ExposureScoreResult(
            eventCount = entity.eventCount,
            exposureScore = entity.score
        )
    }

    override suspend fun saveExposureScore(locationKey: String, result: ExposureScoreResult) {
        exposureCacheDao.insert(
            ExposureCacheEntity(
                locationKey = locationKey,
                eventCount = result.eventCount,
                score = result.exposureScore
            )
        )
    }

    override suspend fun getVulnerabilityCache(locationKey: String): VulnerabilityScoreResult? {
        val entity = vulnerabilityCacheDao.getByKey(locationKey) ?: return null
        return VulnerabilityScoreResult(
            floodScore = entity.floodScore,
            landslideScore = entity.landslideScore,
            vulnerabilityScore = entity.score
        )
    }

    override suspend fun saveVulnerabilityScore(
        locationKey: String,
        result: VulnerabilityScoreResult,
        isInFloodZone: Boolean,
        isInLandslideZone: Boolean
    ) {
        vulnerabilityCacheDao.insert(
            VulnerabilityCacheEntity(
                locationKey = locationKey,
                isInFloodZone = isInFloodZone,
                isInAvalancheZone = isInLandslideZone,
                floodScore = result.floodScore,
                landslideScore = result.landslideScore,
                score = result.vulnerabilityScore
            )
        )
    }

    override suspend fun getGeoScoreCache(locationKey: String): GeoScore? {
        val entity = totalScoreCacheDao.getByKey(locationKey) ?: return null
        return GeoScore(
            locationKey = locationKey,
            hazardScore = entity.hazardScore,
            exposureScore = entity.exposureScore,
            vulnerabilityScore = entity.vulnerabilityScore,
            geoScore = entity.geoScore,
            precipitationScore  = entity.precipitationScore,
            windScore = entity.windScore,
            floodScore = entity.floodScore,
            landslideScore = entity.landslideScore,
            extremeWeatherDaysCount = entity.eventCount
        )
    }

    override suspend fun saveGeoScore(geoScore: GeoScore) {
        totalScoreCacheDao.insert(
            TotalScoreCacheEntity(
                locationKey = geoScore.locationKey,
                hazardScore = geoScore.hazardScore,
                exposureScore = geoScore.exposureScore,
                vulnerabilityScore = geoScore.vulnerabilityScore,
                geoScore = geoScore.geoScore,
                precipitationScore = geoScore.precipitationScore,
                windScore = geoScore.windScore,
                floodScore = geoScore.floodScore,
                landslideScore = geoScore.landslideScore,
                eventCount = geoScore.extremeWeatherDaysCount
            )
        )
    }
}
