package no.uio.ifi.in2000.team20.team20app.data.repository

import android.util.Log
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSourceService
import no.uio.ifi.in2000.team20.team20app.data.model.FrostObservationDataDto
import no.uio.ifi.in2000.team20.team20app.domain.model.ClimateData
import no.uio.ifi.in2000.team20.team20app.domain.model.ClimateObservation

/**
 * Interface for fetching processed Frost API climate data.
 *
 * Why:
 * Makes FrostRepository testable by allowing fake implementations in tests.
 */
interface FrostRepositoryService {
    suspend fun getClimateData(lat: Double, lon: Double): ClimateData
}

/**
 * FrostRepository
 *
 * Responsibility:
 * - Fetch climate data from FrostDataSource
 * - Map DTOs to domain models
 * - Provide stable functions for ViewModels
 *
 * Why:
 * Separates concerns: UI should not care about endpoints or DTO structure.
 */
class FrostRepository(
    private val dataSource: FrostDataSourceService
) : FrostRepositoryService {

    override suspend fun getClimateData(lat: Double, lon: Double): ClimateData {
        val station = dataSource.getStation(lat, lon)
        val observationResponse = dataSource.getObservations(station.id)

        return ClimateData(
            stationId = station.id,
            stationName = station.name,
            observations = observationResponse.data.map { it.toDomain() }
        )
    }

    private fun FrostObservationDataDto.toDomain(): ClimateObservation {
        Log.d("Frost", "elementIds for $referenceTime: ${observations.map { it.elementId }}")
        val tempObs = observations.find { it.elementId == "mean(air_temperature P1M)" }
        val precipObs = observations.find {
            it.elementId == "best_estimate_sum(precipitation_amount P1M)"
        }

        return ClimateObservation(
            time = referenceTime,
            airTemperature = tempObs?.value,
            precipitation = precipObs?.value
        )
    }
}
