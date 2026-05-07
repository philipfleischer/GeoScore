package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.local.Entity.ExposureCacheEntity
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepositoryService
import no.uio.ifi.in2000.team20.team20app.data.repository.ScoreCacheRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.ExposureScoreResult
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXTREME_PRECIPITATION_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.EXTREME_WIND_GUST_THRESHOLD
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_EXTREME_WEATHER_COUNT
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_EXTREME_WEATHER_COUNT
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GetExposureScore @Inject constructor(
    private val frostRepository: FrostRepositoryService,
    private val scoreCacheRepository: ScoreCacheRepository
) {
    suspend fun calculateExposureScore(lat: Double, lon: Double): ExposureScoreResult {

        val locationKey = "%.2f, %.2f".format(lat, lon)
        val cachedResult = scoreCacheRepository.getExposureCache(locationKey)
        if (cachedResult != null) {
            return ExposureScoreResult(
                eventCount = cachedResult.eventCount,
                exposureScore = cachedResult.score
            )
        }

        val result = frostRepository.getWindAndPrecipitationObservations(lat, lon)
        val precipitationResults = result.precipitationValues
        val windResults = result.windValues

        val extremePrecipDates = precipitationResults.filter { it.value >= EXTREME_PRECIPITATION_THRESHOLD }.keys
        val extremeWindDates = windResults.filter { it.value >= EXTREME_WIND_GUST_THRESHOLD }.keys

        //toSet() is used to remove duplicates
        val sumOfextremeWeatherDates = (extremePrecipDates + extremeWindDates).toSet().size

        val exposure = ((sumOfextremeWeatherDates - MIN_EXTREME_WEATHER_COUNT).toDouble() / (MAX_EXTREME_WEATHER_COUNT - MIN_EXTREME_WEATHER_COUNT)) * 100

        val score = max(0.0, min(100.0, exposure))

        scoreCacheRepository.saveExposureScore(
            ExposureCacheEntity(
                locationKey = locationKey,
                eventCount = sumOfextremeWeatherDates,
                score = score
            )
        )

        return ExposureScoreResult(
            eventCount = sumOfextremeWeatherDates,
            exposureScore = score
        )
    }
}
