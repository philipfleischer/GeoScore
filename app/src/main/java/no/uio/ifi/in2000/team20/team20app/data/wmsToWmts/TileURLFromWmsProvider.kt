package no.uio.ifi.in2000.team20.team20app.data.wmsToWmts

import com.google.android.gms.maps.model.UrlTileProvider
import java.net.URL

class TileURLFromWmsProvider(val baseUrl: String, val tileSize: Int): UrlTileProvider(tileSize, tileSize){
    //TODO: Not hardcoded ofc, also tilesize not hardcoded
    val hardcodedUrl = "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap&layers=air_temperature_2m_meps_det_vdiv_2_5km_calculations&crs=EPSG:3857&styles=Temperature_2_m&format=image%2Fpng&transparent=true&time=2026-03-12T21:00:00.000Z&width=256&height=256&bbox="
    //TODO: Depending on whether square wms or uncomment separate logic for y dimension
    override fun getTileUrl(x: Int, y: Int, zoomLevel: Int): URL? {
        val bboxDimension = getZoomLevelsXLength(zoomLevel)
        val resolution = getXYResolutionForXYZ(zoomLevel)
        val bboxXLowerBound = getBboxXLowerBound(bboxDimension, x, resolution)
        val bboxYLowerBound = getBboxYLowerBound(bboxDimension, y, resolution)
        val bbox: String = hardcodedUrl+bboxXLowerBound+","+bboxYLowerBound+","+(bboxXLowerBound+bboxDimension)+","+(bboxYLowerBound+bboxDimension)
        return URL(bbox)

    }
}