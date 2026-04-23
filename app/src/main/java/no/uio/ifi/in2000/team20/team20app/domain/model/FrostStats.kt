package no.uio.ifi.in2000.team20.team20app.domain.model

/**
 * Domain model for aggregated Frost API climate statistics.
 *
 * Responsibility:
 * - Single object passed from repository to ViewModel, combining all five Frost parameters
 *
 * Why:
 * Plain Kotlin — no serialization or data-layer dependencies.
 * The repository maps raw FrostObservationResponseDto objects into this shape.
 */

// Three temperature values per calendar month — computed from 30 years of raw monthly data (1991-2020)
data class FrostTemperatureNormal(
    val mean: Double?,    // 30-year average of mean(air_temperature P1M)
    val maxMean: Double?, // 30-year average of mean(max(air_temperature P1D) P1M)
    val minMean: Double?  // 30-year average of mean(min(air_temperature P1D) P1M)
)

// Two precipitation values per calendar month
data class FrostPrecipitationNormal(
    val rainyDays: Double?,  // number of days with precipitation >= 1mm
    val meanAmount: Double?  // mean precipitation amount in mm
)

// Two snow depth values per calendar month (aggregated from raw 30-year history)
data class FrostSnowNormal(
    val mean: Double?, // mean(surface_snow_thickness P1M)
    val max: Double?   // max(surface_snow_thickness P1M)
)

data class FrostStats(
    // month 1–12 -> temperature normals (mean, mean-max, mean-min)
    val temperatureNormals: Map<Int, FrostTemperatureNormal>?,
    // month 1–12 -> precipitation (rainy days + mean amount)
    val precipitation: Map<Int, FrostPrecipitationNormal>?,
    // month 1–12 -> snow depth (mean + peak), aggregated from raw 30-year history
    val snowDepth: Map<Int, FrostSnowNormal>?,
    // month 1–12 -> mean wind speed in m/s, aggregated from raw 30-year history
    val wind: Map<Int, Double>?,
    // month 1–12 -> mean sunshine duration in hours
    val sunshineNormals: Map<Int, Double>?
)