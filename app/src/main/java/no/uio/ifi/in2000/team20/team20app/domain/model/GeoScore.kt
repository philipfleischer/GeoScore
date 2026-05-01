package no.uio.ifi.in2000.team20.team20app.domain.model

/**
 * Domain model representing flood, landslide or wind risk.
 *
 * Responsibility:
 * - Represent hazard level and related information
 *
 * Why:
 * Used across use cases and ViewModels.
 */
data class WindAndPrecipitationObservationsResult(
    val precipitationValues: Map<String, Double>,
    val windValues: Map<String, Double>
)

data class GeoScore (
    val hazardScore: Double,
    val exposureScore: Double,
    val vulnerabilityScore: Double,
    val geoScore: Double
)