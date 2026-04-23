package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects for Frost API responses.
 *
 * Responsibility:
 * - Represent raw JSON from Frost API endpoints (both v0 and v1)
 *
 * Why:
 * Decouples external API format from domain models.
 * V0 and V1 have different response shapes — both are represented here.
 * V0 (frost.met.no) is used for historical normals/aggregates.
 * V1 (frost-rc.met.no) is used for current/recent observations.
 */

// ─── V0 observation types (frost.met.no/observations/v0.jsonld) ─────────────
// Used for climate normals — historical aggregated data not yet available in V1.

@Serializable
data class FrostV0ObservationResponseDto(
    val data: List<FrostV0ObservationDataDto>
)

// One entry per station per reference time
@Serializable
data class FrostV0ObservationDataDto(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<FrostV0ObservationValueDto>
)

@Serializable
data class FrostV0ObservationValueDto(
    val elementId: String,
    val value: Double
)

// ─── V0 sources types (frost.met.no/sources/v0.jsonld) ──────────────────────

@Serializable
data class FrostV0SourceResponseDto(
    val data: List<FrostV0SourceDto>
)

@Serializable
data class FrostV0SourceDto(
    val id: String,
    val name: String? = null
)

// ─── V1 observation types (frost-rc.met.no/api/v1/obs/) ─────────────────────

// Root response wrapper
@Serializable
data class FrostObservationResponseDto(
    val tseries: List<FrostTimeSeriesDto>
)

// One entry per (station × element) time series
@Serializable
data class FrostTimeSeriesDto(
    val header: FrostTimeSeriesHeaderDto,
    val observations: List<FrostObservationEntryDto>? = null
)

@Serializable
data class FrostTimeSeriesHeaderDto(
    val id: FrostTimeSeriesIdDto,
    val extra: FrostTimeSeriesExtraDto? = null
)

@Serializable
data class FrostTimeSeriesIdDto(
    val stationid: Int,
    val sensor: Int? = null,
    val level: Int? = null,
    val paramid: Int? = null
)

@Serializable
data class FrostTimeSeriesExtraDto(
    val element: FrostElementInfoDto? = null,
    val station: FrostStationInfoDto? = null
)

@Serializable
data class FrostElementInfoDto(
    val id: String,
    val name: String? = null,
    val unit: String? = null
)

@Serializable
data class FrostStationInfoDto(
    val shortname: String? = null
)

// One observation per reference time (one per calendar month for normals)
@Serializable
data class FrostObservationEntryDto(
    val time: String,
    val body: FrostObservationBodyDto
)

// Note: Frost v1 returns value and qualitycode as strings despite the schema showing numbers
@Serializable
data class FrostObservationBodyDto(
    val value: String? = null,
    val qualitycode: String? = null
)

// ─── V1 ranked observations (frost-rc.met.no/api/v1/obs/ranked/get) ─────────
// Response wraps tseries in a "data" object — different shape from the types above.

@Serializable
data class FrostV1ResponseDto(
    val data: FrostV1DataDto
)

@Serializable
data class FrostV1DataDto(
    val tseries: List<FrostV1TimeSeriesDto>
)

@Serializable
data class FrostV1TimeSeriesDto(
    val header: FrostV1HeaderDto,
    val observations: List<FrostV1ObservationDto>? = null
)

@Serializable
data class FrostV1ObservationDto(
    val time: String,
    val body: FrostV1ObservationBodyDto
)

@Serializable
data class FrostV1ObservationBodyDto(
    val value: String
)

@Serializable
data class FrostV1HeaderDto(
    val id: FrostV1IdDto,
    val extra: FrostV1ExtraDto,
    val available: FrostV1AvailableDto
)

@Serializable
data class FrostV1IdDto(
    val stationid: Int
)

@Serializable
data class FrostV1ExtraDto(
    val element: FrostV1ElementDto
)

@Serializable
data class FrostV1ElementDto(
    val id: String
)

@Serializable
data class FrostV1AvailableDto(
    val from: String,
    val to: String? = null
)