package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas

data class MapLayer(
    val name: String,
    val layerId: Int,
    val iconId: Int, // Is there a better way to get the right icon for a MapLayer. The type of R.drawable is int so we could pass those directly.
    val toggled: Boolean, // If we limit to one active layer at a time we can move this to an active layer holder
    val formatter: (xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int) -> String
)

object MapLayers {
    val layers = listOf(
        MapLayer(
            name = "Radon",
            layerId = 0,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.RadonUrlFormatter
        ),
        MapLayer(
            name = "Kvikkleire",
            layerId = 1,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.QuickClayUrlFormatter
        ),
        MapLayer(
            name = "1000 års flomsoner",
            layerId = 2,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.ClimateAdjustedFlood1000YearUrlFormatter
        ),
        MapLayer(
            name = "200 års flomsoner",
            layerId = 3,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.ClimateAdjustedFlood200YearUrlFormatter
        ),
        MapLayer(
            name = "20 års flomsoner",
            layerId = 4,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.ClimateAdjustedFlood20YearUrlFormatter
        ),
        MapLayer( //Denne er ikke viktig. Egentlig fjern når det er annet til å fylle med.
            name = "Naturstein",
            layerId = 5,
            iconId = 0,
            toggled = false,
            formatter = WmsFormatterLambdas.rockUrlFormatter
        )
    )
}