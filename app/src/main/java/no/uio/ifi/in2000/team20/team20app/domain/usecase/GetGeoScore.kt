package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXPOSURESCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.HAZARDSCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.VULNERABILITYSCORE_WEIGHT
import javax.inject.Inject

/**
 * Use case for calculating the GeoScore.
 * @property getExposureScore Use case for calculating the exposure score
 * @property getHazardScore Use case for calculating the hazard score
 * @property getVulnerabilityScore Use case for calculating the vulnerability score
 * @property scoreCacheRepository Repository for caching scores
 */
class GetGeoScore @Inject constructor(
    private val getExposureScore: GetExposureScore,
    private val getHazardScore: GetHazardScore,
    private val getVulnerabilityScore: GetVulnerabilityScore,
    private val scoreCacheRepository: ScoreCacheRepository
) {
    suspend fun calculateGeoScore(lat: Double, lon: Double): GeoScore {
        val locationKey = "%.2f, %.2f".format(lat, lon) //TODO: Standardize lat/lng precision on an app level
        val cachedTotal = scoreCacheRepository.getGeoScoreCache(locationKey)
        //TODO: COMPUTE THE SUB-SCORES IN PARALLEL!!! scope ( .launch .launch .await .await)

        // Always compute sub-scores — they hit their own caches if already stored
        val hazardResult = getHazardScore.calculateHazardScore(lat, lon)
        val vulnerabilityResult = getVulnerabilityScore.calculateVulnerabilityScore(lat, lon)
        val exposureResult = getExposureScore.calculateExposureScore(lat, lon)

        if (cachedTotal != null) {
            return GeoScore(
                locationKey             = locationKey,
                precipitationScore      = hazardResult.precipitationScore,
                windScore               = hazardResult.windScore,
                floodScore              = vulnerabilityResult.floodScore,
                landslideScore          = vulnerabilityResult.landslideScore,
                hazardScore             = cachedTotal.hazardScore,
                exposureScore           = cachedTotal.exposureScore,
                vulnerabilityScore      = cachedTotal.vulnerabilityScore,
                extremeWeatherDaysCount = exposureResult.eventCount,
                geoScore                = cachedTotal.geoScore
            )
        }

        val geoScore = GeoScore(
            locationKey             = locationKey,
            precipitationScore      = hazardResult.precipitationScore,
            windScore               = hazardResult.windScore,
            floodScore              = vulnerabilityResult.floodScore,
            landslideScore          = vulnerabilityResult.landslideScore,
            hazardScore             = hazardResult.hazardScore,
            exposureScore           = exposureResult.exposureScore,
            vulnerabilityScore      = vulnerabilityResult.vulnerabilityScore,
            extremeWeatherDaysCount = exposureResult.eventCount,
            geoScore                = (hazardResult.hazardScore * HAZARDSCORE_WEIGHT) + (exposureResult.exposureScore * EXPOSURESCORE_WEIGHT) + (vulnerabilityResult.vulnerabilityScore * VULNERABILITYSCORE_WEIGHT)
        )

        scoreCacheRepository.saveGeoScore(geoScore)

        return geoScore
    }
}
