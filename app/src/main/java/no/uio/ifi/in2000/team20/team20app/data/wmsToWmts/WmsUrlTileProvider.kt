package no.uio.ifi.in2000.team20.team20app.data.wmsToWmts


import com.google.android.gms.maps.model.UrlTileProvider
import java.net.URL

class WmsUrlTileProviderEpsg3857(
    private val datasetXMinBound: Double = -GMAP_WMS_BOUND,
    private val datasetXMaxBound: Double = GMAP_WMS_BOUND,
    private val datasetYMinBound: Double = -GMAP_WMS_BOUND,
    private val datasetYMaxBound: Double = GMAP_WMS_BOUND,
    tileSize: Int = 256,
    private val urlFormatter: (
        xMinBound: Double,
        yMinBound: Double,
        xMaxBound: Double,
        yMaxBound: Double
    ) -> String
): UrlTileProvider(tileSize, tileSize) {
    private val alteredBounds: Boolean = datasetXMinBound != -GMAP_WMS_BOUND || datasetXMaxBound != GMAP_WMS_BOUND || datasetYMinBound != -GMAP_WMS_BOUND || datasetYMaxBound != GMAP_WMS_BOUND

    override fun getTileUrl(x: Int, y: Int, zoomLevel: Int): URL? {
        val dimensionsBBOX: Double = getZoomLevelBoundingBoxDimensions(zoomLevel)
        val resolutionXY: Int = getZoomLevelXYResolution(zoomLevel)
        val xMin: Double =
            getBoundingBoxXMin(dimensionsBBOX = dimensionsBBOX, resolutionXY = resolutionXY, x = x)
        val yMin: Double =
            getBoundingBoxYMin(dimensionsBBOX = dimensionsBBOX, resolutionXY = resolutionXY, y = y)
        //If no pixels are in the bounding box, return null
        if (alteredBounds &&
            (xMin > datasetXMaxBound || xMin + dimensionsBBOX < datasetXMinBound ||
                    yMin > datasetYMaxBound || yMin + dimensionsBBOX < datasetYMinBound)) {
            return null
        }
        return try {
            URL(urlFormatter(xMin, yMin, xMin + dimensionsBBOX, yMin + dimensionsBBOX))
        }catch(_: Exception){
            null
        }
    }
}

private const val SHIFT_BIT: Int = 1
private const val Y_INVERT_ALIGNER: Int = 1

private fun getZoomLevelBoundingBoxDimensions(zoom: Int): Double {
    return if(zoom == 0){
        2 * GMAP_WMS_BOUND
    }
    else{
        GMAP_WMS_BOUND / (SHIFT_BIT shl (zoom - 1))
    }
}

private fun getZoomLevelXYResolution(zoom: Int): Int{
    return (SHIFT_BIT shl zoom)
}

private fun getBoundingBoxXMin(dimensionsBBOX: Double, resolutionXY: Int, x: Int): Double{
    return if(resolutionXY == 1){
        -GMAP_WMS_BOUND
    }else {
        (x - (resolutionXY / 2)) * dimensionsBBOX
    }
}
private fun getBoundingBoxYMin(dimensionsBBOX: Double, resolutionXY: Int, y: Int): Double {
    return if (resolutionXY == 1) {
        -GMAP_WMS_BOUND
    } else {
        ((resolutionXY / 2) - (y + Y_INVERT_ALIGNER)) * dimensionsBBOX
    }
}