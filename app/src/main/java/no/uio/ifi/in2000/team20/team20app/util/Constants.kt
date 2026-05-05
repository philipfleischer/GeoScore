package no.uio.ifi.in2000.team20.team20app.util

import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.BuildConfig

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
    const val CHATGPT_API_KEY = BuildConfig.CHATGPT_API_KEY

    // MAP DEFAULT SETTINGS
    const val DEFAULT_ZOOM = 8f
    const val ZOOM_ON_LOCATION = 15f
    // IFI as default
    const val DEFAULT_LATITUDE = 59.94376
    const val DEFAULT_LONGITUDE = 10.71889

    const val MAX_ZOOM: Float = 19.0f
    const val MIN_ZOOM: Float = 4.0f

    // RISK THRESHOLDS
    const val SUM_YEARS_ANALYZED = 30

    const val EXTREME_WIND_GUST_THRESHOLD = 40.0
    const val EXTREME_PRECIPITATION_THRESHOLD = 40.0

    const val REF_MAX_PRECIP_OVER_THRESHOLD = 15.0
    const val REF_MAX_WIND_OVER_THRESHOLD = 7.0

    const val MIN_EXTREME_WEATHER_COUNT = 0
    const val MAX_EXTREME_WEATHER_COUNT = 15 * SUM_YEARS_ANALYZED

    const val PRECIPITATION_WEIGHT = 0.65
    const val WIND_WEIGHT = 0.35

    const val FLODD_WEIGHT = 0.45
    const val LANDSLIDE_WEIGHT = 0.55

    const val HAZARDSCORE_WEIGHT = 0.2
    const val EXPOSURESCORE_WEIGHT = 0.4
    const val VULNERABILITYSCORE_WEIGHT = 0.4



    // UI DIMENSIONS
    const val DEFAULT_PADDING_DP = 16
    const val SMALL_PADDING_DP = 8
    const val LARGE_PADDING_DP = 24

    const val CARD_CORNER_RADIUS_DP = 16
    const val MAP_LEGEND_HEIGHT_DP = 120

    const val MEDIUM_SCREEN_WIDTH = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND

    // DATA REFRESH SETTINGS
    const val AUTO_REFRESH_INTERVAL_MINUTES = 2
}