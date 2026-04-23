package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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