package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.local.TotalScoreCacheDao
import no.uio.ifi.in2000.team20.team20app.data.local.TotalScoreCacheEntity
import no.uio.ifi.in2000.team20.team20app.domain.model.GeoScore
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardScoreResult
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXPOSURESCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.HAZARDSCORE_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.VULNERABILITYSCORE_WEIGHT

class GetGeoScore(
    private val getExposureScore: GetExposureScore,
    private val getHazardScore: GetHazardScore,
    private val getVulnerabilityScore: getVulnerabilityScore,
    private val geoScoreDAO: TotalScoreCacheDao
) {
    suspend fun calculateGeoScore(lat: Double, lon: Double): GeoScore {

        val locationKey = "%.2f, %.2f".format(lat,lon)
        val cachedResult = geoScoreDAO.getByKey(locationKey)
        if (cachedResult != null) {
            print("from cached result")
            return GeoScore(
                hazardScore = cachedResult.hazardScore,
                exposureScore = cachedResult.exposureScore,
                vulnerabilityScore = cachedResult.vulnerabilityScore,
                geoScore = cachedResult.geoScore
            )
        }

        val exposureScore = getExposureScore.calculateExposureScore(lat, lon)
        val hazardScore = getHazardScore.calculateHazardScore(lat, lon)
        val vulnerabilityScore = getVulnerabilityScore.calculateVulnerabilityScore(lat, lon)

        val geoScore = GeoScore(
            hazardScore = hazardScore.hazardScore,
            exposureScore = exposureScore,
            vulnerabilityScore = vulnerabilityScore,
            geoScore = (hazardScore * HAZARDSCORE_WEIGHT) + (exposureScore * EXPOSURESCORE_WEIGHT) + (vulnerabilityScore * VULNERABILITYSCORE_WEIGHT)
        )

        geoScoreDAO.insert(
            TotalScoreCacheEntity(
                locationKey = locationKey,
                hazardScore = hazardScore,
                exposureScore = exposureScore,
                vulnerabilityScore = vulnerabilityScore,
                geoScore = geoScore.geoScore
            )
        )

        return geoScore
    }
}
