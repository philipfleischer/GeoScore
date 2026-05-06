package no.uio.ifi.in2000.team20.team20app.data.dto

import kotlinx.serialization.Serializable

//== For Adresse ==//
@Serializable
data class AddressResponse(
    val metadata: AddressMetadata,
    val adresser: List<Address>
)

@Serializable
data class AddressMetadata(
    val side: Int,
    val sokeStreng: String,
    val treffPerSide: Int,
    val viserFra: Int,
    val viserTil: Int,
    val totaltAntallTreff: Int,
    val asciiKompatibel: Boolean
)

@Serializable
data class Address(
    val adressenavn: String? = null,
    val adressetekst: String? = null,
    val kommunenavn: String? = null,
    val poststed: String? = null,
    val postnummer: String? = null,
    val representasjonspunkt: RepresentasjonsPunkt? = null
)

@Serializable
data class RepresentasjonsPunkt(
    val epsg: String,
    val lat: Double,
    val lon: Double
)