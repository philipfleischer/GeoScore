package no.uio.ifi.in2000.team20.team20app.domain.model.urlFormatter


/**
 * Interface for URL formatters.
 */
interface UrlFormatter : (Double, Double, Double, Double, Int) -> String{

    fun getRequiredZoom(): Float

    override operator fun invoke(
        xMin: Double,
        yMin: Double,
        xMax: Double,
        yMax: Double,
        zoom: Int
    ): String
}