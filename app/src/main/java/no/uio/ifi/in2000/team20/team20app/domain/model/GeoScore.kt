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

fun scoreToGrade(score: Double): String = when {
    score < 17 -> "A"
    score < 33 -> "B"
    score < 50 -> "C"
    score < 67 -> "D"
    score < 83 -> "E"
    else       -> "F"
}

data class WindAndPrecipitationObservationsResult(
    val precipitationValues: Map<String, Double>,
    val windValues: Map<String, Double>
)

data class GeoScore (
    val hazardScore: Double,
    val exposureScore: Double,
    val vulnerabilityScore: Double,
    val precipitationScore: Double,
    val windScore: Double,
    val floodScore: Double,
    val landslideScore: Double,
    val extremeWeatherDaysCount: Int,
    val geoScore: Double
)

data class HazardScoreResult (
    val precipitationScore: Double, //0-100
    val windScore: Double, //0-100
    val hazardScore: Double //0-100
)

data class VulnerabilityScoreResult (
    val floodScore: Double, //0-100
    val landslideScore: Double, //0-100
    val vulnerabilityScore: Double //0-100
)

data class ExposureScoreResult (
    val eventCount: Int,
    val exposureScore: Double,
)

data class Report (
    val extremePrecipitationText: String,
    val extremeWindText: String,
    val floodText: String,
    val landslideText: String,

)