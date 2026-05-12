package no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer

import no.uio.ifi.in2000.team20.team20app.domain.model.urlFormatter.UrlFormatter

/**
 * Data class for map layers.
 */
data class MapLayer(
    val type: MapLayerDefinition,
    val layerId: Int,
    val imageURI: String,
    val legendURI: List<Pair<String, String>>,
    val urlFormatter: UrlFormatter
)