package no.uio.ifi.in2000.team20.team20app.util

import no.uio.ifi.in2000.team20.team20app.BuildConfig
import no.uio.ifi.in2000.team20.team20app.domain.model.CreatedLocation

/**
 * Application-wide constants.
 *
 * This file contains configuration values and fixed constants
 * that are used across multiple layers of the application.
 *
 * Examples:
 * - API base URLs
 * - Timeout values
 * - Default map settings
 * - Padding, spacing and UI layout constants
 * - Risk thresholds
 *
 * Why:
 * Centralizes configuration to avoid hardcoded values
 * scattered throughout the codebase.
 * Improves maintainability and readability.
 *
 * NOTE:
 * Sensitive API keys should NOT be hardcoded here in production.
 * They should be stored securely (e.g., in local.properties).
 */

//TODO: This is just a template, need to be revised
object Constants {

    // API CONFIGURATION
    const val FROST_BASE_URL = "https://frost.met.no/"
    const val NVE_BASE_URL = "https://api.nve.no/"
    const val TIMEOUT_SECONDS = 30L
    const val FROST_CLIENT_ID = BuildConfig.FROST_V0_CLIENT_ID
    const val FROST_CLIENT_SECRET = BuildConfig.FROST_V0_CLIENT_SECRET

    // WMS BOUNDS
    const val GMAP_WMS_BOUND: Double = 20037508.34

    // MAP DEFAULT SETTINGS
    const val DEFAULT_ZOOM = 6.5
    const val DEFAULT_NAME = "Bergen"
    const val DEFAULT_LATITUDE = 60.3913
    const val DEFAULT_LONGITUDE = 5.3221
    val DEFAULT_POSITION: CreatedLocation = CreatedLocation(DEFAULT_NAME,DEFAULT_LATITUDE, DEFAULT_LONGITUDE, null)

    const val MAX_ZOOM: Float = 19.0f
    const val MIN_ZOOM: Float = 4.0f

    const val TILE_SIZE = 256

    // RISK THRESHOLDS
    const val LOW_WIND_THRESHOLD = 5.0
    const val MEDIUM_WIND_THRESHOLD = 15.0
    const val HIGH_WIND_THRESHOLD = 25.0

    const val FLOOD_WARNING_LEVEL = 2
    const val AVALANCHE_HIGH_RISK = 4

    // UI DIMENSIONS
    const val DEFAULT_PADDING_DP = 16
    const val SMALL_PADDING_DP = 8
    const val LARGE_PADDING_DP = 24

    const val CARD_CORNER_RADIUS_DP = 16
    const val MAP_LEGEND_HEIGHT_DP = 120

    // DATA REFRESH SETTINGS
    const val AUTO_REFRESH_INTERVAL_MINUTES = 2
}