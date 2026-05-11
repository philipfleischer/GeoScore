package no.uio.ifi.in2000.team20.team20app.util

import android.util.Log
import androidx.core.net.toUri
//TODO: Move base-urls to constants file
//TODO: Figure out if lambdas should have a static resolution based on what suits their dataset or if this should be a parameter
object WmsFormatterLambdas {
    val RockfallUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        "https://kart.nve.no/enterprise/services/SkredSteinAktR/MapServer/WMSServer".toUri()
            .buildUpon()
            .appendQueryParameter("service", "WMS")
            .appendQueryParameter("version", "1.3.0")
            .appendQueryParameter("request", "GetMap")
            .appendQueryParameter("layers", "Utlopsomrade") // Layer name from XML
            .appendQueryParameter("styles", "")
            .appendQueryParameter("crs", "EPSG:3857")
            .appendQueryParameter("bbox", "$xMin,$yMin,$xMax,$yMax")
            .appendQueryParameter("width", "1024")
            .appendQueryParameter("height", "1024")
            .appendQueryParameter("format", "image/png")
            .appendQueryParameter("transparent", "true")
            .build()
            .toString()
    }
    val RadonUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        "https://geo.ngu.no/mapserver/RadonWMS2".toUri()
            .buildUpon()
            .appendQueryParameter("service", "WMS")
            .appendQueryParameter("version", "1.3.0")
            .appendQueryParameter("request", "GetMap")
            .appendQueryParameter("layers", "Radon_aktsomhet") // The layer name from Capabilities
            .appendQueryParameter("styles", "")               // Required but can be empty
            .appendQueryParameter("crs", "EPSG:3857")
            .appendQueryParameter("bbox", "$xMin,$yMin,$xMax,$yMax")
            .appendQueryParameter("width", "1024") // TODO: annen res?
            .appendQueryParameter("height", "1024")
            .appendQueryParameter("format", "image/png")      // Recommended for transparency
            .appendQueryParameter("transparent", "true")
            .build()
            .toString()
    }

    val LandslideAwareness = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        "https://kart.nve.no/enterprise/services/JordFlomskredAktsomhet/MapServer/WMSServer?".toUri()
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

    val QuickClayUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
        "https://kart.nve.no/enterprise/services/SkredKvikkleire2/MapServer/WMSServer".toUri()
            .buildUpon()
            .appendQueryParameter("SERVICE", "WMS")
            .appendQueryParameter("VERSION", "1.3.0")
            .appendQueryParameter("REQUEST", "GetMap")
            .appendQueryParameter("LAYERS", "KvikkleireKartlagtOmrade,KvikkleireFaregrad,KvikkleireRisiko")
            .appendQueryParameter("STYLES", "")
            .appendQueryParameter("CRS", "EPSG:3857") // Common web mercator from the list
            .appendQueryParameter("BBOX", "$xMin,$yMin,$xMax,$yMax")
            .appendQueryParameter("WIDTH", "512")
            .appendQueryParameter("HEIGHT", "512")
            .appendQueryParameter("FORMAT", "image/png") // Recommended for transparency
            .appendQueryParameter("TRANSPARENT", "TRUE")
            .build()
            .toString()

    }
    val ClimateAdjustedFlood20YearUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
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
                .appendQueryParameter("LAYERS", "Flomsone_20arsflom_klima") //Finnes jo ikke!
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

    val ClimateAdjustedFlood200YearUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
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
    val ClimateAdjustedFlood1000YearUrlFormatter = { xMin: Double, yMin: Double, xMax: Double, yMax: Double, zoom: Int ->
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
}