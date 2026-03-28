package no.uio.ifi.in2000.team20.team20app.ui.validation

// Resultatet av addresse valideringen.
// Valid betyr at input kan brukes videre, Invalid betyr at UI kan vise en feilmelding til bruker.
sealed class AddressValidationResult {
    data object Valid : AddressValidationResult()
    data class Invalid(val message: String) : AddressValidationResult()
}

object AddressValidator {

    // Grenser for å unngå tomme, veldig korte eller unødvendig lange søk.
    // Dette gjør input mer forutsigbar før vi sender noe til API-et.
    private const val MIN_LENGTH = 3
    private const val MAX_LENGTH = 100

    // Tillater bokstaver, norske tegn, tall, mellomrom og noen vanlige adressetegn
    private val allowedCharactersRegex =
        Regex("^[a-zA-ZæøåÆØÅ0-9 .,'\\-]+$")

    // Validerer brukerinput før søk.
    // Returnerer enten Valid eller en konkret feilmelding som kan vises i UI.
    fun validate(query: String): AddressValidationResult {
        val trimmed = query.trim()

        return when {
            trimmed.isEmpty() ->
                AddressValidationResult.Invalid("Skriv inn en adresse eller et sted.")

            trimmed.length < MIN_LENGTH ->
                AddressValidationResult.Invalid("Søket må være minst $MIN_LENGTH tegn.")

            trimmed.length > MAX_LENGTH ->
                AddressValidationResult.Invalid("Søket kan ikke være lengre enn $MAX_LENGTH tegn.")

            !allowedCharactersRegex.matches(trimmed) ->
                AddressValidationResult.Invalid("Søket inneholder ugyldige tegn.")

            else ->
                AddressValidationResult.Valid
        }
    }

    fun sanitize(query: String): String {
        return query.trim().replace(Regex("\\s+"), " ")
    }
}