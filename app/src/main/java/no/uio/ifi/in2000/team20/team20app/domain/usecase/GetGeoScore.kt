package no.uio.ifi.in2000.team20.team20app.domain.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.ExposureScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardScoreResult
import no.uio.ifi.in2000.team20.team20app.domain.model.VulnerabilityScoreResult
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXPOSURESCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.HAZARDSCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.VULNERABILITYSCORE_WEIGHT
import javax.inject.Inject

class GetGeoScore @Inject constructor(
    private val getExposureScore: GetExposureScore,
    private val getHazardScore: GetHazardScore,
    private val getVulnerabilityScore: GetVulnerabilityScore,
    private val scoreCacheRepository: ScoreCacheRepository,
    private val frostRepository: FrostRepositoryService
) {
    suspend fun calculateGeoScore(lat: Double, lon: Double): GeoScore {

        val locationKey = "%.2f, %.2f".format(lat, lon)
        val cachedTotal = scoreCacheRepository.getGeoScoreCache(locationKey)
        if (cachedTotal != null) return cachedTotal

        val hazardResult: HazardScoreResult
        val vulnerabilityResult: VulnerabilityScoreResult
        val exposureResult: ExposureScoreResult

        coroutineScope {
            // observations and vulnerability called in parallel
            val observationsDeferred = async { frostRepository.getWindAndPrecipitationObservations(lat, lon) }
            val vulnerabilityDeferred = async { getVulnerabilityScore.calculateVulnerabilityScore(lat, lon) }

            val observations = observationsDeferred.await()
            vulnerabilityResult = vulnerabilityDeferred.await()

            // hazard and exposure use the already fetched observations
            hazardResult = getHazardScore.calculateHazardScore(lat, lon, observations)
            exposureResult = getExposureScore.calculateExposureScore(lat, lon, observations)
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