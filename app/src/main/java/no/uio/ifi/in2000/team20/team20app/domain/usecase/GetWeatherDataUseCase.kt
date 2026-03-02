package no.uio.ifi.in2000.team20.team20app.domain.usecase

/**
 * Retrieves processed weather or climate data for a given location.
 *
 * This UseCase:
 * - Calls WeatherRepository
 * - Optionally aggregates or filters data
 * - Returns clean domain models ready for presentation
 *
 * It represents one clear user intention:
 * "Show me the weather or climate statistics for this area."
 *
 * What happens here:
 * - Fetching relevant weather information
 * - Applying domain-level transformations if needed
 * - Preparing structured data for the ViewModel
 *
 * Why:
 * Separates weather-related business logic from UI and data layers,
 * making the application easier to test and maintain.
 */