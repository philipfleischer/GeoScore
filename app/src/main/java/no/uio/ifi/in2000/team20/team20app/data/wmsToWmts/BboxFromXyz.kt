package no.uio.ifi.in2000.team20.team20app.data.wmsToWmts

import no.uio.ifi.in2000.team20.team20app.util.Constants.X_MAX
import no.uio.ifi.in2000.team20.team20app.util.Constants.Y_MAX
import kotlin.math.pow

//TODO: MOVE aFZI TO CONSTANTS,
// EVEN THOUGH ITS JUST A ONE IT MAKES THE CONVOLUTED LOGIC BEARABLY READABLE
const val accountingForZeroIndexing: Int = 1
//Since the max values from world center equate to half the world we subtract the exponent(zoom) by 1 since base is a power of 2
fun getZoomLevelsXLength(zoom: Int): Double {
    return X_MAX / (2.0.pow(zoom-1))
}

fun getZoomLevelsYLength(zoom: Int): Double {
    return Y_MAX / (2.0.pow(zoom-1))
}

fun getXYResolutionForXYZ(zoom: Int): Int{
    return (2.0.pow(zoom)).toInt()
}

//Just add one zoomLevelsXY length to the val to get the max for BBOX
fun getBboxXLowerBound(bboxDimension: Double, coordinate: Int, resolution: Int): Double{
    return (coordinate - (resolution/2)) * bboxDimension
}
//Compute Y value separately because WMTS says north is min-value while WMS says north is max-value
fun getBboxYLowerBound(bboxDimension: Double, coordinate: Int, resolution: Int): Double{
    return (((resolution/2) - coordinate)-accountingForZeroIndexing)*bboxDimension
}