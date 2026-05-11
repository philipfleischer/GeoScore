package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas


data class MapLayer(
    val name: String,
    val layerId: Int,
    val imageURI: String,
    val legendURI: List<Pair<String, String>>,
    val formatter: (xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int) -> String
)

object MapLayers {
    val layers = listOf(
        MapLayer(
            name = "Ingen",
            layerId = 0,
            imageURI = "file:///android_asset/layerImage/kartlag.png",
            legendURI = listOf(),
            formatter = { _, _, _, _, _ -> "" }
        ),
        MapLayer(
            name = "Flomsoner", //legend: https://kart.nve.no/enterprise/services/Flomsoner2/MapServer/WMSServer?request=GetLegendGraphic%26version=1.3.0%26format=image/png%26layer=Flomsone_200arsflom_klima
            layerId = 1,
            imageURI = "file:///android_asset/layerImage/flooding500x500.jpg",
            legendURI = listOf(
                Pair("Elv og vann", "file:///android_asset/mapLegend/flood/Elv_og_vann.png"),
                Pair("Flomutsatt", "file:///android_asset/mapLegend/flood/Flomutsatt.png"),
                Pair("Lavpunkt", "file:///android_asset/mapLegend/flood/Lavpunkt.png")
            ),
            formatter = WmsFormatterLambdas.ClimateAdjustedFlood200YearUrlFormatter
        ),
        MapLayer(
            name = "Skred", //
            layerId = 2,
            imageURI = "file:///android_asset/layerImage/landslide_awareness500x500.jpg",
            legendURI = listOf(
                Pair("Skredaktsomhet", "file:///android_asset/mapLegend/landslide/Jord_flom_skred_aktsomhet.png")
            ),
            formatter = WmsFormatterLambdas.LandslideAwareness
        ),
        MapLayer(
            name = "Steinsprang", //legend: https://kart.nve.no/enterprise/services/SkredSteinAktR/MapServer/WMSServer?request=GetLegendGraphic%26version=1.3.0%26format=image/png%26layer=Steinsprang-AktsomhetOmrader
            layerId = 3,
            imageURI = "file:///android_asset/layerImage/stoneslide500x500.jpg",
            legendURI = listOf(
                Pair("Utløpsområde", "file:///android_asset/mapLegend/rockfall/Utlopsomrade.png"),
                Pair("Utløsningsområde", "file:///android_asset/mapLegend/rockfall/Utlosningsomrade.png")
            ),
            formatter = WmsFormatterLambdas.RockfallUrlFormatter
        )
//        MapLayer(
//            name = "Radon",
//            layerId = 0,
//            iconId = 0,
//            toggled = false,
//            formatter = WmsFormatterLambdas.RadonUrlFormatter
//        ),
//        MapLayer(
//            name = "Kvikkleire",
//            layerId = 1,
//            iconId = 0,
//            toggled = false,
//            formatter = WmsFormatterLambdas.QuickClayUrlFormatter
//        ),
//        MapLayer(
//            name = "1000 års flomsoner",
//            layerId = 2,
//            iconId = 0,
//            toggled = false,
//            formatter = WmsFormatterLambdas.ClimateAdjustedFlood1000YearUrlFormatter
//        ),
//        MapLayer(
//            name = "20 års flomsoner",
//            layerId = 4,
//            iconId = 0,
//            toggled = false,
//            formatter = WmsFormatterLambdas.ClimateAdjustedFlood20YearUrlFormatter
//        ),
//        MapLayer( //Denne er ikke viktig. Egentlig fjern når det er annet til å fylle med.
//            name = "Naturstein",
//            layerId = 5,
//            iconId = 0,
//            toggled = false,
//            formatter = WmsFormatterLambdas.rockUrlFormatter
//        )
    )
}