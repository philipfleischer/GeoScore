package no.uio.ifi.in2000.team20.team20app.domain.usecase

/**
 * Retrieves hazard risk information for a given location.
 *
 * This UseCase:
 * - Calls HazardRepository
 * - Applies any necessary business rules
 * - Returns a clean domain model to the ViewModel
 *
 * It represents one clear user intention:
 * "Show me the hazard risk for this area."
 *
 * Why:
 * Separates business logic from UI and data layers,
 * improving structure, readability and testability.
 */