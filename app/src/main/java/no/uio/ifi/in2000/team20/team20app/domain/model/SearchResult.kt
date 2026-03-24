package no.uio.ifi.in2000.team20.team20app.domain.model

data class SearchResult(
    val locations: List<Location> = emptyList(),
)

data class Location(
    val name: String?,
    val kommune: String?,
    val fylke: String?,
    val lat: Double?,
    val lon: Double?
)