package no.uio.ifi.in2000.team20.team20app.domain.model

/**
 * Domain model for climate data from the Frost API.
 *
 * Responsibility:
 * - Clean representation used by UI and ViewModel
 *
 * Why:
 * Decouples UI from raw Frost API structure.
 */
data class ClimateData(
    val stationId: String,
    val stationName: String,
    val observations: List<ClimateObservation>
)

data class ClimateObservation(
    val time: String,
    val airTemperature: Double?,
    val precipitation: Double?
)
