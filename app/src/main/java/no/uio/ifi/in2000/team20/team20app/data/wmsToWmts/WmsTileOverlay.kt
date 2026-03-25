package no.uio.ifi.in2000.team20.team20app.data.wmsToWmts


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.TileOverlay
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.TileOverlayState
import com.google.maps.android.compose.rememberTileOverlayState

/**
 * The maximum bound for Web Mercator (EPSG:3857) projection when the world is cropped to square,
 * which is the case for GoogleMap
 */
const val GMAP_WMS_BOUND: Double = 20037508.34

@Composable
@GoogleMapComposable
fun WmsTileOverlayEpsg3857(
    wmsDataSetXMinBound: Double = -GMAP_WMS_BOUND,
    wmsDataSetXMaxBound: Double = GMAP_WMS_BOUND,
    wmsDataSetYMinBound: Double = -GMAP_WMS_BOUND,
    wmsDataSetYMaxBound: Double = GMAP_WMS_BOUND,
    tileSize: Int = 256,
    urlFormatter: (
        xMinBound: Double,
        yMinBound: Double,
        xMaxBound: Double,
        yMaxBound: Double
    ) -> String,
    state: TileOverlayState = rememberTileOverlayState(),
    fadeIn: Boolean = true,
    transparency: Float = 0f,
    visible: Boolean = true,
    zIndex: Float = 0f,
    onClick: (TileOverlay) -> Unit = {},
) {
    val wmsUrlTileProvider = remember(
        wmsDataSetXMinBound,
        wmsDataSetXMaxBound,
        wmsDataSetYMinBound,
        wmsDataSetYMaxBound,
        tileSize,
        urlFormatter
    ){
        WmsUrlTileProviderEpsg3857(
            datasetXMinBound = wmsDataSetXMinBound,
            datasetXMaxBound = wmsDataSetXMaxBound,
            datasetYMinBound = wmsDataSetYMinBound,
            datasetYMaxBound = wmsDataSetYMaxBound,
            tileSize = tileSize,
            urlFormatter = urlFormatter
        )
    }
    TileOverlay(
        tileProvider = wmsUrlTileProvider,
        state = state,
        fadeIn = fadeIn,
        transparency = transparency,
        visible = visible,
        zIndex = zIndex,
        onClick = onClick,
    )
}
