package no.uio.ifi.in2000.team20.team20app.domain.usecase

import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_norway_parcipitation_max
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_norway_parcipitation_min
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_norway_wind_max
import no.uio.ifi.in2000.team20.team20app.util.Constants.P95_norway_wind_min
import kotlin.math.max

import kotlin.math.min

class GetHazardScore(
    private val frostRepository: FrostRepository
) {
    suspend fun getHazardScore(lat: Double, lon: Double): Double {
        val windAndParcipitationObservationsResult = frostRepository.getWindAndParcipitationObservations(lat, lon)
        val parcipitationResults = windAndParcipitationObservationsResult.precipitationValues
        val windResults = windAndParcipitationObservationsResult.windValues
        val P95Precipitation = findP95ForPrecipitation(parcipitationResults)
        val P95Wind = findP95ForWind(windResults)

        val parcipitationScore = 0.65 * max(0.0,min(100.0,((P95Precipitation - P95_norway_parcipitation_min) / (P95_norway_parcipitation_max - P95_norway_parcipitation_min))*100))
        val windScore = 0.35 * max(0.0,min(100.0,((P95Wind - P95_norway_wind_min) / (P95_norway_wind_max - P95_norway_wind_min))*100))

        return parcipitationScore + windScore
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