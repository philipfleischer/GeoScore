package no.uio.ifi.in2000.team20.team20app.data.repository.map

import no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer.MapLayer
import no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer.MapLayerDefinition
import javax.inject.Inject

/**
 * Repository for map layers.
 * This is in accordance with the Creator and Information Expert design pattern.
 * This repository handles the instantiation.
 */
interface MapLayerRepository{
    fun getMapLayers(): List<MapLayer>
}

class MapLayerRepositoryImpl @Inject constructor(
    private val urlFormatterRepository: UrlFormatterRepository
): MapLayerRepository {
    override fun getMapLayers(): List<MapLayer> =
        MapLayerDefinition.entries.map { definition ->
            when (definition) {
                MapLayerDefinition.DEFAULT ->
                    MapLayer(
                        type = definition,
                        layerId = 0,
                        imageURI = "file:///android_asset/layerImage/kartlag.png",
                        legendURI = listOf(),
                        urlFormatter = urlFormatterRepository.getFormatter(definition)
                    )

                MapLayerDefinition.FLOOD ->
                    MapLayer(
                        type = definition,
                        layerId = 1,
                        imageURI = "file:///android_asset/layerImage/flooding500x500.jpg",
                        legendURI = listOf(
                            Pair(
                                "Elv og vann",
                                "file:///android_asset/mapLegend/flood/Elv_og_vann.png"
                            ),
                            Pair(
                                "Flomutsatt",
                                "file:///android_asset/mapLegend/flood/Flomutsatt.png"
                            ),
                            Pair("Lavpunkt", "file:///android_asset/mapLegend/flood/Lavpunkt.png")
                        ),
                        urlFormatter = urlFormatterRepository.getFormatter(definition)
                    )

                MapLayerDefinition.LANDSLIDE ->
                    MapLayer(
                        type = definition,
                        layerId = 2,
                        imageURI = "file:///android_asset/layerImage/landslide_awareness500x500.jpg",
                        legendURI = listOf(
                            Pair(
                                "Skredaktsomhet",
                                "file:///android_asset/mapLegend/landslide/Jord_flom_skred_aktsomhet.png"
                            )
                        ),
                        urlFormatter = urlFormatterRepository.getFormatter(definition)
                    )

                MapLayerDefinition.ROCKSLIDE ->
                    MapLayer(
                        type = definition,
                        layerId = 3,
                        imageURI = "file:///android_asset/layerImage/stoneslide500x500.jpg",
                        legendURI = listOf(
                            Pair(
                                "Utløpsområde",
                                "file:///android_asset/mapLegend/rockfall/Utlopsomrade.png"
                            ),
                            Pair(
                                "Utløsningsområde",
                                "file:///android_asset/mapLegend/rockfall/Utlosningsomrade.png"
                            )
                        ),
                        urlFormatter = urlFormatterRepository.getFormatter(definition)
                    )
            }
        }
}