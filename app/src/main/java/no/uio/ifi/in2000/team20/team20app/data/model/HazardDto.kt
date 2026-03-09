package no.uio.ifi.in2000.team20.team20app.data.model

/**
 * Data Transfer Object for hazard-related responses.
 *
 * Responsibility:
 * - Represent raw map or risk data
 *
 * Why:
 * Avoid coupling domain models directly to API responses.
 *
 * -------Example use--------
 * NVE API:
 * {
 *   "location": "Oslo",
 *   "hazardType": "Flood",
 *   "riskLevel": 3,
 *   "description": "Moderate flood risk",
 *   "coordinates": {
 *     "lat": 59.91,
 *     "lon": 10.75
 *   }
 * }
 *
 * Dto:
 * data class HazardDto(
 *     val location: String,
 *     val hazardType: String,
 *     val riskLevel: Int,
 *     val description: String,
 *     val latitude: Double,
 *     val longitude: Double
 * )
 */