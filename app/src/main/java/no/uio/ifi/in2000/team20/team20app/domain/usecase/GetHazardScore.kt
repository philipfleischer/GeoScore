package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.HazardScoreResult
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXTREME_PRECIPITATION_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXTREME_WIND_GUST_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.PRECIPITATION_WEIGHT
import no.uio.ifi.in2000.team20.team20app.util.Constants.REF_MAX_PRECIP_OVER_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.REF_MAX_WIND_OVER_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.WIND_WEIGHT
import javax.inject.Inject
import kotlin.math.min

class GetHazardScore @Inject constructor(
    private val frostRepository: FrostRepositoryService,
    private val scoreCacheRepository: ScoreCacheRepository
) {
    suspend fun calculateHazardScore(lat: Double, lon: Double): HazardScoreResult {

        //make lat and lon more vague and check if location is in the database
        val locationKey = "%.2f, %.2f".format(lat, lon)
        val cachedResult = scoreCacheRepository.getHazardCache(locationKey)
        if (cachedResult != null) {
            return cachedResult
        }

        val windAndPrecipitationObservationsResult = frostRepository.getWindAndPrecipitationObservations(lat, lon)
        val precipitationResults = windAndPrecipitationObservationsResult.precipitationValues
        val windResults = windAndPrecipitationObservationsResult.windValues

        val extremePrecipDays = precipitationResults.values.filter { it > EXTREME_PRECIPITATION_THRESHOLD }

        val precipOverTheThreshold: Double
        if (extremePrecipDays.isEmpty()) {
            precipOverTheThreshold = 0.0
        } else {
            //How much over the threshold is the extreme precipitation on average
            precipOverTheThreshold = extremePrecipDays.map { it - EXTREME_PRECIPITATION_THRESHOLD }.average()
        }

        val extremeWindDays = windResults.values.filter { it > EXTREME_WIND_GUST_THRESHOLD }
        val windOverTheThreshold: Double
        if (extremeWindDays.isEmpty()) {
            windOverTheThreshold = 0.0
        } else {
            //How much over the threshold is the extreme wind on average
            windOverTheThreshold = extremeWindDays.map { it - EXTREME_WIND_GUST_THRESHOLD }.average()
        }

        //0-100 for wind and precipitation scores
        val precipitationScore = min(100.0, (precipOverTheThreshold / REF_MAX_PRECIP_OVER_THRESHOLD) * 100)
        val windScore = min(100.0, (windOverTheThreshold / REF_MAX_WIND_OVER_THRESHOLD) * 100)
        val score = PRECIPITATION_WEIGHT * precipitationScore + WIND_WEIGHT * windScore

        val result = HazardScoreResult(
            precipitationScore = precipitationScore,
            windScore = windScore,
            hazardScore = score
        )

        scoreCacheRepository.saveHazardScore(locationKey, result)

        return result
    }
}
