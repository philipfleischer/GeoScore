package no.uio.ifi.in2000.team20.team20app.data.api

/**
 * NVE ArcGIS API route constants.
 *
 * The HTTP client is provided by Hilt (NetworkModule).
 */

object NveRoutes {
    private const val BASE = "https://kart.nve.no/enterprise/rest/services"

    const val FLOM = "$BASE/Flomaktsomhet/MapServer/1/query"

    const val SKRED = "$BASE/Skredfaresoner1/MapServer/3/query"

}