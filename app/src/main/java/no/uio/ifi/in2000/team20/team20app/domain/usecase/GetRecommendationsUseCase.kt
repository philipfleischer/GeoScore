package no.uio.ifi.in2000.team20.team20app.domain.usecase

/**
 * Generates climate adaptation recommendations based on hazard or weather data.
 *
 * This UseCase:
 * - Calls one or more repositories (e.g. HazardRepository, WeatherRepository)
 * - Interprets risk levels and conditions
 * - Transforms raw data into actionable recommendations for the user
 *
 * It represents one clear user intention:
 * "What should I do given the current climate risk?"
 *
 * What happens here:
 * - Domain-level reasoning (e.g. high flood risk → recommend drainage measures)
 * - Simple rule evaluation or combination of multiple data sources
 *
 * Why:
 * Keeps decision logic out of the ViewModel,
 * ensuring that UI only displays results rather than computing them.
 */