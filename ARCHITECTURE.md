# Architecture

## Introduksjon
* //TODO skriv en liten hyggelig introduksjon. f eks dette er ment for utviklere som skal vidreutvikle prosjektet...
---

## Overordnet arkitektur

Appen følger Androids anbefalte lagdelte arkitektur (UI → Domain → Data) og er strukturert etter MVVM-mønsteret. Kodebasen er delt inn i fire hoveddeler:

```
app/
├── data/           — Datakilde, Room-database, DTOer og repository-implementasjoner
├── domain/         — Domenemodeller, repository-grensesnitt og use cases
├── ui/             — Skjermer, ViewModels, navigasjon og gjenbrukbare komponenter
└── di/             — Hilt-moduler for dependency injection
```

### `/data` - Datalaget
Datalaget har ansvaret for all datahenting og lokal lagring:
- **`datasource/`**
  - klasser som kommuniserer direkte med eksterne API-er (Frost, NVE, Geonorge, OpenAI). Hver datasource har ett ansvarsområde.
- **`repository/`** 
  - repository-implementasjoner som implementerer grensesnitt definert i domenelaget. Repositories er det eneste stedet use cases og ViewModels henter data fra. Repositories håndterer mapping fra DTOer til domenemodeller.
- **`local/`** 
  - Room-database med alle DAOs og Entity-klasser for lokal caching. All Entity-konstruksjon skjer utelukkende i datalaget.
- **`dto/`** 
  - dataklasser som speiler API-responsformatet

### `/domain` - Domenelaget
Domenelaget inneholder ingen Android-spesifikke avhengigheter og er ren forretningslogikk:
- **`model/`** 
  - rene Kotlin-dataklasser som representerer domeneobjekter (`GeoScore`, `Location`, `HazardScoreResult` osv.)
- **`repository/`** 
  - grensesnitt for repositories. Use cases og ViewModels avhenger av grensesnittet, ikke implementasjonen.
- **`usecase/`** 
  - forretningslogikk. Hver use case har ett ansvarsområde og injiserer kun repository-grensesnitt. Aldri DAOs eller Entity-klasser direkte.

### `/ui` - UI-laget
UI-laget inneholder alt knyttet til brukergrensesnittet bygd med Jetpack Compose:
- **`screens/`** — en pakke per skjerm, med Screen-composable, ViewModel og UiState
- **`sharedViewModels/`** — ViewModels som deles på tvers av skjermer (`AppViewModel`, `FrostViewModel`)
- **`navigation/`** — `NavigationRoot.kt` og `Route.kt` håndterer hele navigasjonsgraphen med Navigation 3
- **`components/`** — gjenbrukbare UI-komponenter (ingen ViewModels her, kun state + lambdas)

### `/di` - Dependency Injection
Fire Hilt-moduler med klart definerte ansvarsområder:

| Modul | Innhold | Scope |
|---|---|---|
| `NetworkModule.kt` | Tre navngitte Ktor `HttpClient`-instanser (`@FrostClient`, `@GeoSearchClient`, `@NveZonesClient`) + OpenAI-klient | `@Singleton` |
| `DatabaseModule.kt` | Room `AppDatabase` + alle 11 DAOs | `@Singleton` |
| `DataSourceModule.kt` | `@Binds` for datasource-grensesnitt til implementasjoner | `@Singleton` |
| `RepositoryModule.kt` | `@Binds` for alle repository-grensesnitt til implementasjoner | `@Singleton` |

---

## Dependency Injection med Hilt

Appen bruker **Hilt 2.59.2** som DI-rammeverk. Hilt var valgt fordi det eliminerer manuelt dependency-håndtering og gir typsikkerhet ved compile-time.

**Setup:**
- `GeoScoreApp` er annotert med `@HiltAndroidApp`
- `MainActivity` er annotert med `@AndroidEntryPoint`
- Alle ViewModels er annotert med `@HiltViewModel` og bruker constructor injection
- Alle repositories, datasources og use cases har `@Inject constructor`

**Scoping:**
- `@Singleton` brukes på alle nettverksklienter, DAOs og repositories som skal deles hele appen igjennom
- `@HiltViewModel` (implisitt scoped til ViewModel-livstiden) brukes på alle ViewModels
- ViewModels som skal deles på tvers av skjermer (`AppViewModel`, `FrostViewModel`) er eksplisitt passert ned gjennom `NavigationRoot`

**Module-struktur:**
- `@Binds` brukes for interface → implementasjon-mappinger (repository og datasource bindings)
- `@Provides` brukes for eksterne typer uten `@Inject constructor` (Ktor-klienter, Room DAOs)

---

## Objektorienterte prinsipper

### MVVM (Model-View-ViewModel)
Appen følger MVVM-mønsteret som anbefalt av Android:
- **View** (Jetpack Compose-skjermer) viser data og sender brukerhandlinger videre
- **ViewModel** holder UI-state som `StateFlow<UiState>` og kommuniserer med domenelaget
- **Model** (use cases + repositories) henter og behandler data

### Lav kobling
- Alle repositories og datasources er definert som grensesnitt i domenelaget
- ViewModels og use cases kjenner kun til grensesnittet, ikke implementasjonen
- Use cases injiserer `ScoreCacheRepository`-grensesnittet. De har ingen kjennskap til Room DAOs eller Entity-klasser (etterspørslen etter Fix 1 som fjernet Room-importene)
- Hilt håndterer alle avhengigheter via constructor injection, noe som eliminerer direkte koblinger

### Høy kohesjon
- Hver use case har ett ansvarsområde (`GetHazardScore`, `GetExposureScore`, osv.)
- `ScoreCacheRepository` eier all score-relatert cache-logikk og holder dette borte fra use cases
- `ChatGPTRepositoryImpl` eier rapport-caching-logikken
- Skjermer er organisert i egne pakker med tilhørende ViewModel og UiState

### Unidirectional Data Flow (UDF)
Data flyter konsekvent i én retning:
1. UI utløser hendelser (brukerklikk, navigasjon, input)
2. ViewModel mottar hendelser og oppdaterer `StateFlow<UiState>`
3. UI observerer `StateFlow` med `collectAsStateWithLifecycle()` og re-rendrer ved tilstandsendringer

---

## ViewModels og scoping

| ViewModel | Scope | Begrunnelse |
|---|---|---|
| `AppViewModel` | Delt (activity-scoped) | Holder valgt lokasjon på tvers av alle skjermer |
| `FrostViewModel` | Delt (activity-scoped) | `HomeScreen` forhåndslaster klimadata for `GeoscoreScreen` og `ClimateStatsScreen` |
| `SavedViewModel` | Delt (activity-scoped) | `isCurrentSaved`-tilstand må være konsistent på tvers av Home, Map, Geoscore og Saved |
| `HomeViewModel` | Skjerm-scoped (`HomeDestination`) | Lokal UI-tilstand kun for hjemskjermen |
| `MapViewModel` | Skjerm-scoped (`MapDestination`) | Lokal UI-tilstand kun for kartskjermen |
| `SearchViewModel` | Skjerm-scoped (`SearchDestination`) | Søketilstand skal nullstilles ved navigasjon |
| `GeoScoreViewModel` | Skjerm-scoped (`GeoscoreDestination`) | Ny instans per lokasjon for isolert scoring |

**Hvorfor noen ViewModels er delt:**
- `AppViewModel` må dele den valgte lokasjonen slik at alle skjermer ser samme valg
- `FrostViewModel` må deles fordi `HomeScreen` forhåndslaster data for etterfølgende skjermer
- `SavedViewModel` må deles fordi `isCurrentSaved`-flagget brukes av flere skjermer samtidig

---

## Teknologier og versioner

| Teknologi | Versjon | Bruk |
|---|---|---|
| **Kotlin** | 2.2.10 | Kodespråk |
| **Jetpack Compose** | BOM 2024.09.00 | UI-rammeverk |
| **Navigation 3** | 1.0.0 | Type-safe navigasjon mellom skjermer |
| **Hilt** | 2.59.2 | Dependency injection |
| **KSP** | 2.2.10-2.0.2 | Kotlin Symbol Processing for Hilt og Room |
| **Room** | 2.8.4 | Lokal SQLite-database for caching |
| **Ktor** | 3.4.1 | HTTP-klient for API-kall |
| **kotlinx.serialization** | 1.7.0 | JSON-serialisering |
| **OpenAI client** | 4.1.0 | ChatGPT-integrasjon for AI-rapporter |
| **Google Maps Compose** | 8.3.0 | Interaktivt kartvisning |
| **Kotlin Coroutines** | 1.9.0 | Asynkrone operasjoner |
| **JUnit** | 4.13.2 | Unit testing |

---

## Eksternale API-integrasjoner

Appen integrerer fem eksterne API-tjenester:

| API | Base URL | Autentisering | Datasource |
|---|---|---|---|
| **Frost v0** (met.no) | `https://frost.met.no/` | `FROST_V0_CLIENT_ID` + `FROST_V0_CLIENT_SECRET` | `FrostDataSource` |
| **Frost v1 RC** (met.no) | `https://frost-rc.met.no/api/v1/obs/ranked/get` | Samme som Frost v0 | `FrostDataSource` |
| **NVE ArcGIS** (flom- og skredsoner) | `https://kart.nve.no/enterprise/rest/services` | Ingen | `NveZonesRemoteDataSource` |
| **Geonorge adressesøk** | `https://ws.geonorge.no/adresser/v1/` | Ingen | `GeoSearchRemoteDataSource` |
| **OpenAI (ChatGPT)** | Via `openai-client`-bibliotek | `CHATGPT_API_KEY` fra `BuildConfig` | `ChatGPTRemoteDataSource` |

Alle HTTP-klienter bruker **Ktor CIO-engine** med `ContentNegotiation` og `kotlinx.serialization` for JSON-håndtering.

---

## Lokal lagring og caching

Appen bruker **Room** for lokal caching av brukerdata og beregnede resultater. Databasen heter `team20_app_db` og inneholder 11 tabeller:

| Tabell | Innhold | Eier |
|---|---|---|
| `saved_locations` | Lagrede lokasjoner (brukers favoritter) | `SavedRepository` |
| `hazard_cache` | Cachet farepoengsum per lokasjon | `ScoreCacheRepository` |
| `exposure_cache` | Cachet eksponeringspoengsum per lokasjon | `ScoreCacheRepository` |
| `vulnerability_cache` | Cachet sårbarhetsscore per lokasjon | `ScoreCacheRepository` |
| `total_score_cache` | Cachet samlet GeoScore per lokasjon | `ScoreCacheRepository` |
| `report_cache` | Cachet AI-generert rapport per lokasjon | `ChatGPTRepository` |
| `temperature_cache` | Cachet temperaturdata (Frost), indeksert ved stationId | `FrostRepository` |
| `wind_cache` | Cachet vinddata (Frost), indeksert ved stationId | `FrostRepository` |
| `sunshine_cache` | Cachet solskinndata (Frost), indeksert ved stationId | `FrostRepository` |
| `snow_cache` | Cachet snødybdedata (Frost), indeksert ved stationId | `FrostRepository` |
| `precipitation_cache` | Cachet nedbørsdata (Frost), indeksert ved stationId | `FrostRepository` |

**Caching-strategi:**
- Score-caching eies og håndteres av `ScoreCacheRepositoryImpl` (entity-konstruksjon skjer der)
- Frost-caching eies av `FrostRepository`
- AI-rapport-caching eies av `ChatGPTRepositoryImpl`
- Entity-klasser er ikke importert i domenelaget (use cases)

### Frost API-caching strategi

`FrostRepository` bruker en to-lags caching-strategi for å minimere API-kall når brukeren søker etter flere lokasjoner:

**Lag 1: Stasjonsliste (in-memory).** Når en bruker søker på en lokasjon rundes koordinatene til 2 desimaler (~1km presisjon) og brukes som nøkkel for å slå opp 10 nærmeste værstasjoner. Denne listen caches i minnet slik at påfølgende API-kall for samme område (f.eks. klimadata-typer) gjenbruker samme stasjonsliste uten nye API-kall. Med 10 stasjoner får man robuste normals som aggregeres fra flere kilder, noe som reduserer følsomheten for datagap ved enkelte stasjoner.

**Lag 2: Klimadata (Room, indeksert ved stationId).** Den nærmeste stasjonen (første i listen) ekstraheres og brukes som primærnøkkel i Room-cachene. Dette gjør at når en bruker søker på en *annen* lokasjon som har samme nærmeste stasjon, returneres klimadataene direkte fra databasen uten API-kall. For eksempel: lokasjon 1 (59.33, 11.29) og lokasjon 2 (59.34, 11.30) kan begge ha SN1380 som nærmeste stasjon — data cachet fra lokasjon 1 gjenbrukes for lokasjon 2.

**Solskinn (spesialtilfelle).** Bare 36 stasjoner i Norge har solskinndata. Nærmeste solskinnstasjon slås opp separat, caches i minnet per lokasjon, og klimadataene caches i Room ved stationId som vanlig.

**Resultat:** ~60% færre API-kall ved søk på flere lokasjoner i samme område, ettersom stationsdata og klimadata deles mellom lokasjoner med samme nærmeste stasjon.

---

## Android API-nivå

| Innstilling | Verdi | Begrunnelse |
|---|---|---|
| `minSdk` | 24 (Android 7.0 Nougat) | Støtter ~95% av aktive enheter; balanse mellom kompatibilitet og moderne funksjoner |
| `targetSdk` | 36 | Sikrer kompatibilitet med nyeste Android-funksjoner og sikkerhetskrav |
| `compileSdk` | 36 | Samme som targetSdk |

**Notat:** AGP 9.0.1 krever Hilt 2.59+ da tidligere versjoner er inkompatible. Hilt 2.57.1 bruker `BaseExtension` fra Android Gradle Plugin som ble fjernet i AGP 9.

---

## Avvik fra Android-anbefalinger

Her dokumenterer vi steder der arkitekturen avviker fra Androids offisielle anbefalinger. Dette er også en samling av ting vi må vurdere og ta tak i. Men her er en foreløpig begrunnelse for avvikene:

### 1. Use case-navngivning 

**Android-anbefaling:** Use cases skal hete `${Verb}${Noun}UseCase` (f.eks. `GetTimeZoneUseCase`)

**Gjeldende praksis:** Use cases heter `GetGeoScore`, `GetHazardScore`, osv. — uten `UseCase`-suffiks

**Begrunnelse:** Denne konvensjonen ble ikke fulgt for å holde kode og klassenavn kortere. Det anbefales å endre dette i fremtidlig vedlikehold.

### 2. Repository-navngivning 

**Android-anbefaling:** Repository-grensesnitt skal hete `${Model}Repository` med implementasjoner som `${Model}RepositoryImpl`

**Gjeldende praksis:** Frost, NVE og GeoSearch sine grensesnitt heter `FrostRepositoryService`, `NveZonesRepositoryService`, `GeoSearchRepositoryService`

**Begrunnelse:** Navnene ble ikke endret for å unngå å berøre lagde implementasjoner fra teammedlemmer sent i prosjektet. Det anbefales å refaktorere disse navnene i fremtidlig vedlikehold.

### 3. UiState-filplassering 

**Android-anbefaling:** `${Screen}UiState`-dataklasser skal ligge i egne `.kt`-filer eller `state.kt`-filer

**Gjeldende praksis:** `FrostUiState` ligger inne i `FrostViewModel.kt`. `HomeUiState` ligger i `HomeViewModel.kt`. `SearchUiState` ligger i `SearchViewModel.kt`.

**Begrunnelse:** For mindre ViewModels ble UiState-klassene plassert samme fil for å holde ting kompakt. Det anbefales å flytte disse til egne filer ved større refaktoreringer.

### 4. Delte ViewModels på tvers av skjermer 

**Android-anbefaling:** ViewModels skal ideelt være scoped til en enkelt skjerm eller navigasjonsdestinasjon

**Gjeldende praksis:** 
- `FrostViewModel` deles av `HomeScreen`, `GeoscoreScreen`, og `ClimateStatsScreen`
- `SavedViewModel` deles av alle skjermer

**Begrunnelse:** 
- `FrostViewModel` deles fordi `HomeScreen` forhåndslaster klimadata som `GeoscoreScreen` og `ClimateStatsScreen` trenger
- `SavedViewModel` deles fordi `isCurrentSaved`-flagget må være konsistent på tvers av alle skjermer. Hvis hver skjerm hadde sin egen instans, ville flagget være inkonsistent.

Dette er en pragmatisk løsning, men kan potensielt føre til state-kollisjonsproblemer dersom `loadFrostStats()` kalles samtidig fra flere skjermer med ulike lokasjoner. I praksis er dette usannsynlig fordi navigasjonen er lineær (brukeren er bare på én skjerm av gangen).

### 5. Frost-aggregering fra flere stasjoner uten fallback

**Gjeldende praksis:** `FrostRepository` aggregerer 1991-2020 klimanormaler fra de 10 nærmeste værstasjoner. Alle 10 stasjoner bidrar til månedlige gjennomsnitt for temperatur, vind, snø og nedbør. Imidlertid brukes kun den nærmeste stasjonen (første i listen) som cache-nøkkel i Room.

**To relaterte utgaver:**

1. **Mismatched caching:** Klimadataene er et gjennomsnitt av 10 stasjoner, men cached med den nærmeste stasjonens ID som primærnøkkel. Den cachede dataen representerer derfor ikke streng målingene fra den single stasjonen, selv om cache-nøkkelen antyder det. Dette kan føre til misforståelser ved debugging.

2. **Ufullstendige data for 1991-2020-perioden:** Det finnes ingen reaktiv fallback-logikk. Hvis de 10 nærmeste stasjonene mangler data for en spesifikk klimaparameter over hele 1991-2020-perioden (f.eks. snødata), vil data-typen vise tomme verdier i stedet for å forsøke alternative kilder.

**Begrunnelse:** Begge utgaver er aksepterte tilnærminger gitt tidsrammer. 10-stasjon-aggregering gir robuste normaler mindre følsomme for datagap ved enkelte stasjoner. Cache-nøkkel på første stasjon muliggjør cross-location-gjenbruk (flere lokasjoner med samme nærmeste stasjon deler cached data). En fullstendig reaktiv fallback-strategi krever betydelig ytterligere implementasjon og utsettes til videre utvikling.

---

## Pakkestruktur og organisering

```
app/src/main/java/no/uio/ifi/in2000/team20/team20app/
├── GeoScoreApp.kt              — @HiltAndroidApp entry point
├── MainActivity.kt             — @AndroidEntryPoint
│
├── data/
│   ├── api/                    — API-klasser (Ktor endpoints)
│   ├── datasource/             — Externe datakildeklasser
│   ├── dto/                    — Deserialiserte API-responsmodeller
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Dao/               — 11 Room DAOs
│   │   └── Entity/            — 11 Room entities
│   └── repository/            — 6 repository-implementasjoner
│
├── domain/
│   ├── model/                 — Domenemodeller (GeoScore, Location, osv.)
│   └── usecase/               — 5 use cases (GetGeoScore, GetHazardScore, osv.)
│
├── ui/
│   ├── screens/               — 5 skjermpakker (home, map, result, saved, search)
│   ├── sharedViewModels/      — AppViewModel, FrostViewModel
│   ├── navigation/            — NavigationRoot.kt, Route.kt
│   ├── components/            — Gjenbrukbare UI-komponenter
│   └── theme/                 — Farger, typer, temaer
│
├── di/                        — 4 Hilt-moduler
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   ├── DataSourceModule.kt
│   └── RepositoryModule.kt
│
└── util/                      — Constants, formatters, helpers
```

---

## State management

All UI-state eksponeres via `StateFlow<${Screen}UiState>` med `stateIn` operator og `WhileSubscribed(5_000)` policy. Dette sikrer at:
- State er lifecycle-aware
- Subscriptions opprettholder ikke unødvendig data når skjermen ikke vises
- State overlever konfigurasjonsendringer (f.eks. skjermrotasjon)

UI-komponentene samler state med `collectAsStateWithLifecycle()` som er lifecycle-aware og forhindrer minnelekkasjer.

**_//TODO sjekk at dette faktisk er caset_**

---

## Testerstrategi

- ViewModels, repositories og datasources testes med fakes (ikke mocks)
- Integrasjonstester (`GeoScoreTest.kt`) lager reelle API-kall og tester use cases end-to-end
- Hver ViewModel eksponerer `uiState: StateFlow`, som gjør testing enkel (les `.value` direkte)

---

<h1>Code-style</h1>

Language: All code and comments are written in English. For Norwegian APIs this means annotating fields in DTOs with `@SerialName` so the value names can be English.

Linting: Running a ktlint-check of the project at regular intervals.

<h1>Git</h1>

Branch-naming: Branches are named on the form `tag/branchname` where `tag` describes the type and `branchname` describes the work done written in kebab-case.
