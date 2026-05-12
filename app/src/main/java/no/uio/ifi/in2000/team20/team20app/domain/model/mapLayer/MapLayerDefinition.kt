package no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer

/**
 * Enum for map layers.
 * This class keeps track of all the map-layers in the app.
 */
enum class MapLayerDefinition(
    val title: String
){
    DEFAULT("Standard"),
    FLOOD("Flom"),
    LANDSLIDE("Skred"),
    ROCKSLIDE("Steinsprang")
}