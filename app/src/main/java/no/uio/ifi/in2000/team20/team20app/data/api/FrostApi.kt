package no.uio.ifi.in2000.team20.team20app.data.api

/**
 * Frost API route constants.
 *
 * The HTTP client is provided by Hilt (NetworkModule).
 * Responsibility:
 * - Define API endpoint URLs only
 * - No business logic
 */

object FrostRoutes {
    // V1 - frost-rc.met.no: has current/recent observations, lacks historical aggregates
    const val OBSERVATIONS_V1 = "https://frost-rc.met.no/api/v1/obs/ranked/get"
    // V0 - frost.met.no: has historical normals and monthly aggregates
    const val OBSERVATIONS_V0 = "https://frost.met.no/observations/v0.jsonld"
    const val SOURCES_V0 = "https://frost.met.no/sources/v0.jsonld"
    const val AVAILABLE_TIMESERIES_V0 = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld"
}