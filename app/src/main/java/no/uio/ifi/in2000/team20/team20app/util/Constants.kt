package no.uio.ifi.in2000.team20.team20app.util

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

    // MAP DEFAULT SETTINGS
    const val DEFAULT_ZOOM = 6.5
    const val DEFAULT_LATITUDE = 60.4720
    const val DEFAULT_LONGITUDE = 8.4689

    const val MAX_ZOOM = 14.0
    const val MIN_ZOOM = 4.0

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