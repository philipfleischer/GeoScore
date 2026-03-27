package no.uio.ifi.in2000.team20.team20app.domain.model

import kotlinx.serialization.Serializable

data class SearchResult(
    val locations: List<Location> = emptyList(),
)

@Serializable
data class Location(
    val address: String,
    val name: String = address,
    val municipality: String? = null,
    val county: String? = null,
    val lat: Double,
    val lon: Double
)