package no.uio.ifi.in2000.team20.team20app.domain.model

/**
 * Represents adaptation recommendations for users.
 *
 * Responsibility:
 * - Contain climate adaptation advice
 *
 * Why:
 * Required by case to provide actionable recommendations.
 */
data class Recommendation(
    val title: String,          // f.eks. "Sikre drenering"
    val description: String     // Utfyllende råd til brukeren, evt en link til hvor de kan finne det
)