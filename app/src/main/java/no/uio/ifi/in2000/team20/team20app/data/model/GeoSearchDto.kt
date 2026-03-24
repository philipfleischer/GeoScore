package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoResponse(
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