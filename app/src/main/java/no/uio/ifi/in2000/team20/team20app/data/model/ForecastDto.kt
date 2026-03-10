package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val properties: PropertiesDto
)

@Serializable
data class PropertiesDto(
    val timeseries: List<TimeseriesDto>
)

@Serializable
data class TimeseriesDto(
    val time: String,
    val data: DataDto
)

@Serializable
//kan være null
data class DataDto(
    val instant: InstantDto?,

    )

@Serializable
//kan være null
data class InstantDto(
    val details: InstantDetailsDto?,
)

@Serializable
data class InstantDetailsDto (
    val air_temperature: Float,
    val relative_humidity: Float,
    val wind_speed: Float
)