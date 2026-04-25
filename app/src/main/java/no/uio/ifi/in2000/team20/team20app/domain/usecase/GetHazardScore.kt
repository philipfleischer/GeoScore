package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_NORWAY_PRECIPITATION_MAX
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_NORWAY_PRECIPITATION_MIN
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_NORWAY_WIND_MAX
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_NORWAY_WIND_MIN

import kotlin.math.max

import kotlin.math.min

class GetHazardScore(
    private val frostRepository: FrostRepository
) {
    suspend fun calculateHazardScore(lat: Double, lon: Double): Double {
        val windAndPrecipitationObservationsResult = frostRepository.getWindAndPrecipitationObservations(lat, lon)
        val precipitationResults = windAndPrecipitationObservationsResult.precipitationValues
        val windResults = windAndPrecipitationObservationsResult.windValues
        val P95Precipitation = findP95ForPrecipitation(precipitationResults.values.toList())
        val P95Wind = findP95ForWind(windResults.values.toList())

        val precipitationScore = 0.65 * max(0.0,min(100.0,((P95Precipitation - P95_NORWAY_PRECIPITATION_MIN) / (P95_NORWAY_PRECIPITATION_MAX - P95_NORWAY_PRECIPITATION_MIN))*100))
        val windScore = 0.35 * max(0.0,min(100.0,((P95Wind - P95_NORWAY_WIND_MIN) / (P95_NORWAY_WIND_MAX - P95_NORWAY_WIND_MIN))*100))

        return precipitationScore + windScore
    }

    fun findP95ForWind(values: List<Double>): Double {
        val sorted = values.sorted()
        val index = (sorted.size * 0.95).toInt()
        return sorted[index]
    }

    fun findP95ForPrecipitation(values: List<Double>): Double {
        val sorted = values.sorted()
        val index = (sorted.size * 0.95).toInt()
        return sorted[index]
    }
}