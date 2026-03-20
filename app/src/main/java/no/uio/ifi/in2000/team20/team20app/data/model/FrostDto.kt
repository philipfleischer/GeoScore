package no.uio.ifi.in2000.team20.team20app.data.model

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects for Frost API responses.
 *
 * Responsibility:
 * - Represent raw JSON from frost.met.no
 *
 * Why:
 * Decouples external API format from domain models.
 */

@Serializable
data class FrostSourceResponseDto(
    val data: List<FrostSourceDto>
)

@Serializable
data class FrostSourceDto(
    val id: String,
    val name: String
)

@Serializable
data class FrostObservationResponseDto(
    val data: List<FrostObservationDataDto>
)

// One entry per referenceTime from the API response
@Serializable
data class FrostObservationDataDto(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<FrostObservationValueDto>
)

@Serializable
data class FrostObservationValueDto(
    val elementId: String,
    val value: Double
)
