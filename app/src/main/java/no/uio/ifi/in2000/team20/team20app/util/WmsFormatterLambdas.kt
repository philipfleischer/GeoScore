package no.uio.ifi.in2000.team20.team20app.util

import android.util.Log
import androidx.core.net.toUri

object WmsFormatterLambdas {
    val flood1000YearUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        //Log.d("WMS URL made with zoom", "$zoom")
        if(zoom < 12){ //example for how to only support reasonable zoom-levels.
            ""
        }
        else {
            "https://kart.nve.no/enterprise/services/Flomsoner2/MapServer/WMSServer?".toUri()
                .buildUpon()
                .appendQueryParameter("SERVICE", "WMS")
                .appendQueryParameter("VERSION", "1.3.0")
                .appendQueryParameter("REQUEST", "GetMap")
                .appendQueryParameter("LAYERS", "Flomsone_1000arsflom_klima")
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
    val rockUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        "https://geo.ngu.no/mapserver/NatursteinWMS3?".toUri()
            .buildUpon()
            .appendQueryParameter("SERVICE", "WMS")
            .appendQueryParameter("VERSION", "1.3.0")
            .appendQueryParameter("REQUEST", "GetMap")
            .appendQueryParameter("LAYERS","Skifer_og_hellestein_samlet,Granitt_og_andre_storkningsbergarter_samlet,Kleberstein_og_serpentinitt_samlet,Marmor_og_kalkstein_samlet,Gneis_samlet,Sandstein_og_konglomerat_samlet,Brynestein_samlet,Annen_blokkstein_samlet,Kvernstein_samlet,Murestein_samlet")
            .appendQueryParameter("BGCOLOR", "0xfffefd")
            .appendQueryParameter("STYLES","default")
            .appendQueryParameter("EXCEPTIONS","inimage")
            .appendQueryParameter("CRS", "EPSG:3857")
            .appendQueryParameter("WIDTH", "256")
            .appendQueryParameter("HEIGHT", "256")
            .appendQueryParameter("FORMAT", "image/png")
            .appendQueryParameter("TRANSPARENT", "true")
            .appendQueryParameter("BBOX", "$xMin,$yMin,$xMax,$yMax")
            .build()
            .toString()


    }
    //val fylkeUrlRettFraAI = "https://wms.geonorge.no/skwms1/wms.adm_enheter2?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&LAYERS=fylker&STYLES=default&CRS=EPSG:3857&WIDTH=256&HEIGHT=256&FORMAT=image/png&TRANSPARENT=TRUE&BBOX="
    //val fylkeUrlFormatterer = {xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int -> "$fylkeUrlRettFraAI$xMin,$yMin,$xMax,$yMax" }
}