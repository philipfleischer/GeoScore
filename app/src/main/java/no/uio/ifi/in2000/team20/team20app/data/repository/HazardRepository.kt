package no.uio.ifi.in2000.team20.team20app.data.repository

/**
 * HazardRepository
 *
 * What this class is:
 * - A single entry point for all "hazard" data used by the app (e.g. flood, avalanche, wind risk layers).
 *
 * What it should do:
 * - Fetch hazard-related data from one or more data sources (network, local cache, mock data).
 * - Convert raw API/DTO models into the app's internal models (domain/UI models), so the rest of the app
 *   does not need to know how the API looks.
 * - Hide details like URLs, query params, parsing, and error handling behind simple functions.
 *
 * Why it exists:
 * - Keeps ViewModels/UseCases clean: they call repository functions instead of talking directly to APIs.
 * - Makes it easier to replace the data source later (new API, caching, offline support) without rewriting UI.
 */
class HazardRepository(
    // private val hazardDataSource: HazardDataSource
) {
    // TODO: Add suspend functions like:
    // suspend fun getHazardOverview(location: Location): HazardOverview
}