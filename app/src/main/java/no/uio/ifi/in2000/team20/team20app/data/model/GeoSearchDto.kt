package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

//== For Location ==//
@Serializable
data class LocationResponse(
    val metadata: MetadataDto,
    val navn: List<NavnItemsDto>
)

@Serializable
data class MetadataDto(
    val side: Int,
    val sokeStreng: String,
    val totaltAntallTreff: Int,
    val treffPerSide: Int,
    //val utkoordsys: Int,
    val viserFra: Int,
    val viserTil: Int
)

@Serializable
data class NavnItemsDto(
    val fylker: List<FylkerItemsDto>,
    val kommuner: List<KommuneItemsDto>,
    val navneobjekttype: String,
    val navnestatus: String,
    val representasjonspunkt: RespresentasjonsPunktDto,
    val skrivemåte: String,
    val skrivemåtestatus: String,
    val språk: String,
    val stedsnummer: Int,
    val stedstatus: String
)

@Serializable
data class FylkerItemsDto(
    val fylkesnavn: String,
    val fylkesnummer: String
)

@Serializable
data class KommuneItemsDto(
    val kommunenavn: String,
    val kommunenummer: String
)

@Serializable
data class RespresentasjonsPunktDto(
    val nord: Double,
    val øst: Double
)


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