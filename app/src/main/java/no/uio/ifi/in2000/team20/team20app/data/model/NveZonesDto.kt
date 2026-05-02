package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

//generic DTO class for ArcGis response
//if Features is not empty then the point is in either a landslide or flood zone
@Serializable
data class ArcGisResponseDto(
    val features: List<ArcGisFeatureDto> = emptyList()
)

@Serializable
data class ArcGisFeatureDto(
    val attributes: ArcGisAttributesDto
)

@Serializable
data class ArcGisAttributesDto(
    // Both for flood and landslide
    val objectid: Int? = null,
    val objekttype: String? = null,
    val opphav: String? = null,
    val tematiskkvalitet: String? = null,
    val noyaktighet: Int? = null,
    val malemetode: Int? = null,
    val oppdateringsdato: Long? = null,
    val globalid: String? = null,

    // Specific for Flood
    val vassomr: String? = null,
    val dtmkilde: String? = null,
    val dtmkildedato: Long? = null,
    val vannkilde: String? = null,
    val vannkildedato: Long? = null,
    val prosjekturl: String? = null,

    // Specific for Landslide
    val skredtype: String? = null,
    val gjentaksintervall: Int? = null,
    val sannsynlighet: String? = null,
    val kommune: String? = null
)