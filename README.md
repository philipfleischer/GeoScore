## Hvordan kjøre applikasjonen
- Appen er ikke publisert noe sted
- Appen kan brukes som en tilsendt APK eller fra kildekode.

- Som per gruppelærer er API-nøkler hardkodet inn som strenger for å gjøre sensuren enklere.

## Avhengigheter
- Appens krever en Android SDK på 24 eller høyere (Android 7). 
- Appens eneste forutsatte tillatelse er internett-tilkobling

## Biblioteker
- Jetpack Compose - UI
- Navigation3 - Jetpack Navigation
- Hilt - Dependency injection
- JUnit - Testing
- Coil - Bilder
- Kotlinx Serialization - Serialisering
- Kotlinx Coroutines - Coroutines
- Room - Database
- Material 3 - Material Design
- OpenAI - Natural Language Processing
  - Et API for språkmodeller
- Google Maps Compose - Kartløsning
  - Et google repo for bruk av Google Maps i Compose-apper
- Compose Charts - Grafisk presentasjon av data
  - Et open-source bibliotek for grafer i Jetpack Compose

## Plugins
- Kotlin serialization - Gjør klasser serializable ved å annotere de som @Serializable
- Kotlin parcelable - Gjør klasser parcelable ved å annotere de som @Parcelize
  - Dette brukes for å restaurere tilstand ved prosess-død
- KSP - Kotlin symbol prosessor
- Hilt - Dependency injection

## Errors
- IDE gir følgende:
  - 2 deprecated metoder. Dette skyldes oppgradering av bibliotek versjon. Løsningene er forholdvis enkle, men vi har ikke tilstrekkelig tid til å verifisere robusthet.
  - 1 assigned verdi blir aldri lest. Til tross for det blir UI'en vår helt rar om vi fjerner den så vi har kommentert denne med IKKE RØR.
- LogCat gir følgende:
  - Noen frame skips, men vi har GoogleMap som er et ganske UI-intensivt element.
  - GoogleMap gir også fra seg en haug med Flogger-logg advarsler fra seg. Etter vår undersøkning kommer dette fra dens interne implementasjon.  

## Team 20
- David Hovde
- Jurius Abdo
- Matilda Wold Dahl
- Philip Fleischer
- Veronica Corsepius Melen
- Victoria Reinseth-Tollefsen
