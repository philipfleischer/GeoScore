package no.uio.ifi.in2000.team20.team20app.data.repository

import no.uio.ifi.in2000.team20.team20app.data.local.Dao.ExposureCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.HazardCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Dao.VulnerabilityCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.HazardCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.TotalScoreCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.local.Entity.VulnerabilityCacheEntity
import javax.inject.Inject

/**
 * Repository responsible for caching computed geo-scores.
 *
 * Responsibility:
 * - Provide a single data-layer entry point for reading and writing all
 *   score-related cache tables (hazard, exposure, vulnerability, total).
 *
 * Why:
 * Keeps Room DAO access in the data layer, decoupling use cases from
 * direct database dependencies.
 */
interface ScoreCacheRepository {
    // Hazard
    suspend fun getHazardCache(locationKey: String): HazardCacheEntity?
    suspend fun saveHazardScore(entity: HazardCacheEntity)

    // Exposure
    suspend fun getExposureCache(locationKey: String): ExposureCacheEntity?
    suspend fun saveExposureScore(entity: ExposureCacheEntity)

    // Vulnerability
    suspend fun getVulnerabilityCache(locationKey: String): VulnerabilityCacheEntity?
    suspend fun saveVulnerabilityScore(entity: VulnerabilityCacheEntity)

    // Total / composite GeoScore
    suspend fun getGeoScoreCache(locationKey: String): TotalScoreCacheEntity?
    suspend fun saveGeoScore(entity: TotalScoreCacheEntity)
}

class ScoreCacheRepositoryImpl @Inject constructor(
    private val hazardCacheDao: HazardCacheDao,
    private val exposureCacheDao: ExposureCacheDao,
    private val vulnerabilityCacheDao: VulnerabilityCacheDao,
    private val totalScoreCacheDao: TotalScoreCacheDao
) : ScoreCacheRepository {

    override suspend fun getHazardCache(locationKey: String): HazardCacheEntity? =
        hazardCacheDao.getByKey(locationKey)

    override suspend fun saveHazardScore(entity: HazardCacheEntity) =
        hazardCacheDao.insert(entity)

    override suspend fun getExposureCache(locationKey: String): ExposureCacheEntity? =
        exposureCacheDao.getByKey(locationKey)

    override suspend fun saveExposureScore(entity: ExposureCacheEntity) =
        exposureCacheDao.insert(entity)

    override suspend fun getVulnerabilityCache(locationKey: String): VulnerabilityCacheEntity? =
        vulnerabilityCacheDao.getByKey(locationKey)

    override suspend fun saveVulnerabilityScore(entity: VulnerabilityCacheEntity) =
        vulnerabilityCacheDao.insert(entity)

    override suspend fun getGeoScoreCache(locationKey: String): TotalScoreCacheEntity? =
        totalScoreCacheDao.getByKey(locationKey)

    override suspend fun saveGeoScore(entity: TotalScoreCacheEntity) =
        totalScoreCacheDao.insert(entity)
}
