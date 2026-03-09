
## Prosjektstruktur og arkitektur

### Arkitekturvalg

Prosjektet er bygget med:
- MVVM (Model–View–ViewModel)
- Manuell Dependency Injection
- Jetpack Navigation (Navigation 3 / NavHost)
- Repository-pattern
- Modulær og skalerbar struktur

Formålet med denne strukturen er å sikre:
- God separasjon av ansvar
- Testbarhet
- Skalerbarhet
- Vedlikeholdbar kode

⸻

### Mappestruktur

Prosjektet følger denne strukturen:
```
app/
└── src/main/java/no/uio/ifi/in2000/team20/team20app/
    ├── data/
    │   ├── datasource/
    │   ├── repository/
    │   ├── model/
    │   └── api/
    │
    ├── domain/
    │   ├── model/
    │   └── usecase/
    │
    ├── ui/
    │   ├── navigation/
    │   ├── screens/
    │   │   ├── home/
    │   │   ├── map/
    │   │   ├── details/
    │   │   └── settings/
    │   └── components/
    │
    ├── viewmodel/
    │
    ├── di/
    │
    └── util/
```

⸻

### Lagvis forklaring

#### data/

Ansvar: Datatilgang og ekstern kommunikasjon.

Inneholder:
- API-klienter (Frost API, NVE WMS, osv.)
- Datasources (Remote / Local)
- Repository-implementasjoner
- Data-modeller

Eksempel:
```
data/api/FrostApi.kt
data/datasource/WeatherRemoteDataSource.kt
data/repository/WeatherRepositoryImpl.kt
```

Dette laget vet hvordan data hentes, men ikke hvordan det vises.

⸻

### domain/

Ansvar: Forretningslogikk.

Her defineres:
- Domene-modeller
- Use cases

Eksempel:
```
domain/usecase/GetFloodRiskUseCase.kt
domain/model/FloodRisk.kt
```

Dette laget:
- Vet ingenting om Android UI
- Vet ingenting om Retrofit/Ktor
- Inneholder ren logikk

Dette gjør systemet mer testbart.

⸻

### ui/

Ansvar: Presentasjon.

Struktur:
```
ui/
 ├── navigation/
 ├── screens/
 └── components/

navigation/
- NavHost
- Route-definisjoner
- Navigasjonslogikk

screens/
    Hver skjerm har sin egen mappe:
    home/
        HomeScreen.kt
        HomeUiState.kt
    map/
        MapScreen.kt
    details/
        EventDetailsScreen.kt

components/
    Gjenbrukbare UI-komponenter:
    HazardCard.kt
    WindLegend.kt
    TopBar.kt
```

Dette hindrer duplisering.

⸻

### viewmodel/

Her ligger alle ViewModels.

Eksempel:
```
HomeViewModel.kt
MapViewModel.kt
```

ViewModel:
- Henter data via UseCases
- Eksponerer UiState
- Håndterer brukerinteraksjon
- Skal ikke inneholde UI-kode

⸻

### di/

Manuell dependency injection.

Eksempel:
```
AppContainer.kt
```

Her opprettes:
- Repository-instans
- Datasource-instans
- UseCase-instans

Disse sendes videre til ViewModels.

Dette gjør at vi:
- Unngår globale singletons
- Unngår Hilt (hvis ikke brukt)
- Har eksplisitt kontroll over avhengigheter

⸻

### util/

Hjelpeklasser:
- Extensions
- Formattere
- Konstanter
- Mapper-funksjoner

⸻

### Dataflyt (MVVM)
1. UI trigger en handling
2. ViewModel kaller UseCase
3. UseCase kaller Repository
4. Repository henter data fra DataSource
5. Resultat returneres tilbake gjennom lagene
6. UI observerer StateFlow og oppdateres

Dette sikrer:
- Ingen direkte API-kall i UI
- Ingen Android-kode i domain
- Tydelig ansvar i hvert lag

⸻

## Case 3 naturhendelser spesifikt:

Case 3 (Naturhendelser) krever:
- Flere datakilder (Frost, NVE, WMS)
- Kartvisualisering
- Klimastatistikk
- Risikoanalyse
- Anbefalinger
