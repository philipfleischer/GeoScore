package no.uio.ifi.in2000.team20.team20app.domain.model

import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.team20.team20app.data.dto.Address
import no.uio.ifi.in2000.team20.team20app.data.dto.AddressMetadata
import no.uio.ifi.in2000.team20.team20app.util.Constants.HTTP_OK

data class SearchResult(
    val locations: List<Location> = emptyList(),
    // For setting the potential error message in SearchScreen
    val status: Int = HTTP_OK
)

data class AddressResponseWrapper(
    val metadata: AddressMetadata?,
    val addresses: List<Address>,
    val status: Int
)

@Serializable
data class Location(
    val address: String,
    val name: String = address,
    val municipality: String? = null,
    val county: String? = null,
    val lat: Double,
    val lon: Double,
    val savedAt: Long = 0L
)