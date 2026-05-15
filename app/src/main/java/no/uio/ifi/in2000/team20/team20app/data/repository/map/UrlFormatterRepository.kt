package no.uio.ifi.in2000.team20.team20app.data.repository.map

import androidx.core.net.toUri
import no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer.MapLayerDefinition
import no.uio.ifi.in2000.team20.team20app.domain.model.urlFormatter.UrlFormatter
import javax.inject.Inject

interface UrlFormatterRepository{
    fun getFormatter(definition: MapLayerDefinition): UrlFormatter
}

class UrlFormatterRepositoryImpl @Inject constructor() : UrlFormatterRepository {
    override fun getFormatter(definition: MapLayerDefinition): UrlFormatter {
        return when (definition) {
            MapLayerDefinition.DEFAULT -> DefaultUrlFormatter
            MapLayerDefinition.FLOOD -> FloodUrlFormatter
            MapLayerDefinition.LANDSLIDE -> LandslideUrlFormatter
            MapLayerDefinition.ROCKSLIDE -> RockslideUrlFormatter
        }
    }
}

object DefaultUrlFormatter : UrlFormatter {
    override fun getRequiredZoom(): Float = 0f

    override fun invoke(
        xMin: Double,
        yMin: Double,
        xMax: Double,
        yMax: Double,
        zoom: Int
    ): String =
        ""
}

object FloodUrlFormatter : UrlFormatter {
    private const val REQUIRED_ZOOM: Float = 12.0f // The zoom level the API requires before returning data

    override fun getRequiredZoom(): Float = REQUIRED_ZOOM

    override fun invoke(
        xMin: Double,
        yMin: Double,
        xMax: Double,
        yMax: Double,
        zoom: Int
    ): String {
        if (zoom < REQUIRED_ZOOM) return ""
        return "https://kart.nve.no/enterprise/services/Flomsoner2/MapServer/WMSServer?".toUri()
            .buildUpon()
            .appendQueryParameter("SERVICE", "WMS")
            .appendQueryParameter("VERSION", "1.3.0")
            .appendQueryParameter("REQUEST", "GetMap")
            .appendQueryParameter("LAYERS", "Flomsone_200arsflom_klima")
            .appendQueryParameter("STYLES", "")
            .appendQueryParameter("CRS", "EPSG:3857")
            .appendQueryParameter("WIDTH", "256")
            .appendQueryParameter("HEIGHT", "256")
            .appendQueryParameter("FORMAT", "image/png")
            .appendQueryParameter("TRANSPARENT", "true")
            .appendQueryParameter("BBOX", "$xMin,$yMin,$xMax,$yMax")
            .build()
            .toString()
    }
}

object LandslideUrlFormatter : UrlFormatter {
    private const val REQUIRED_ZOOM: Float = 14.0f // The zoom level the API requires before returning data

    override fun getRequiredZoom(): Float = REQUIRED_ZOOM

    override fun invoke(
        xMin: Double,
        yMin: Double,
        xMax: Double,
        yMax: Double,
        zoom: Int
    ): String {
        if (zoom < REQUIRED_ZOOM) return ""
        return "https://kart.nve.no/enterprise/services/JordFlomskredAktsomhet/MapServer/WMSServer?".toUri()
            .buildUpon()
            .appendQueryParameter("SERVICE", "WMS")
            .appendQueryParameter("VERSION", "1.3.0")
            .appendQueryParameter("REQUEST", "GetMap")
            .appendQueryParameter("LAYERS", "Jord_flomskred_aktsomhetsomrader58338")
            .appendQueryParameter("STYLES", "")
            .appendQueryParameter("CRS", "EPSG:3857")
            .appendQueryParameter("BBOX", "$xMin,$yMin,$xMax,$yMax")
            .appendQueryParameter("WIDTH", "256")
            .appendQueryParameter("HEIGHT", "256")
            .appendQueryParameter("FORMAT", "image/png")
            .appendQueryParameter("TRANSPARENT", "true")
            .build()
            .toString()
    }
}

object RockslideUrlFormatter : UrlFormatter {
    private const val REQUIRED_ZOOM: Float = 14.0f // The zoom level the API requires before returning data

    override fun getRequiredZoom(): Float = REQUIRED_ZOOM

    override fun invoke(
        xMin: Double,
        yMin: Double,
        xMax: Double,
        yMax: Double,
        zoom: Int
    ): String {
        if (zoom < REQUIRED_ZOOM) return ""
        return "https://kart.nve.no/enterprise/services/SkredSteinAktR/MapServer/WMSServer".toUri()
            .buildUpon()
            .appendQueryParameter("service", "WMS")
            .appendQueryParameter("version", "1.3.0")
            .appendQueryParameter("request", "GetMap")
            .appendQueryParameter("layers", "Utlopsomrade") // Layer name from XML
            .appendQueryParameter("styles", "")
            .appendQueryParameter("crs", "EPSG:3857")
            .appendQueryParameter("bbox", "$xMin,$yMin,$xMax,$yMax")
            .appendQueryParameter("width", "256")
            .appendQueryParameter("height", "256")
            .appendQueryParameter("format", "image/png")
            .appendQueryParameter("transparent", "true")
            .build()
            .toString()
    }
}