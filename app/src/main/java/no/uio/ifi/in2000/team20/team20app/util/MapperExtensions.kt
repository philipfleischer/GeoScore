package no.uio.ifi.in2000.team20.team20app.util

/**
 * Mapper extension functions.
 *
 * This file contains extension functions that convert
 * API response models (DTOs) into internal domain models.
 *
 * Why is this needed?
 * - API models often reflect raw JSON structure
 * - Domain models represent how the app logically uses the data
 *
 * By separating mapping logic:
 * - Repositories stay clean and focused
 * - Domain layer remains independent of API structure
 * - Changes in API format affect only the mapper
 */

// Temporary template example (TODO: REMOVE LATER)

data class ExampleDto(
    val value_from_api: Int
)

data class ExampleModel(
    val value: Int
)

fun ExampleDto.toDomain(): ExampleModel {
    return ExampleModel(
        value = this.value_from_api
    )
}