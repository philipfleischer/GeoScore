# MODELING.md
---
## Use Case 1: Vise historisk klimadata for en valgt lokasjon

### Mål
Brukeren skal kunne se historisk klimadata for en bolig de er interessert i.

### Aktører
Primær aktør: Bruker
Sekundær aktører: Geonorge adresse-API, Frost API (V0), Google maps composable

### Betingelser
Prebetingelser:
- Brukeren er tilkoblet internett
- Brukeren har åpnet appen

Postbetingelser:
- Historisk klimadata for valgt lokasjon er vist på skjermen
- Rådataen er cachet lokalt i Room DB for fremtidig bruk

### Hovedflyt
1. Brukeren åpner appen og ser hjemskjermen med et søkefelt
2. Brukeren trykker på søkefeltet og skriver inn ønsket adresse
3. Appen sender et API-kall til Geonorge sitt adresse-API
4. Geonorge returnerer en liste med adresseforslag
5. Brukeren velger en adresse fra listen
6. Appen viser den valgte adressen som en pin på kartet
7. Brukeren trykker på "Generer rapport" for den valgte lokasjonen
8. Brukeren blir sent til et loading screen
9. Appen sender API-kall til Frost V0 og V1 og henter klimadata
   (nedbør, vind, temperatur, osv.) for lokasjonen
10. Frost returnerer klimadataen
11. Appen cacher rådataen i Room DB og aggregerer den
12. Appen navigerer brukeren til rapportskjermen
13. Brukeren trykker på "Historisk klimadata"
14. Brukeren blir sendt til historisk klimadata-skjermen og kan se dataen plottet i grafer

### Alternativ flyt

**A1: Brukeren velger lokasjon via kart (alternativ til steg 1–6)**
A1.1 Brukeren trykker på kartikonet og navigerer til kartskjermen
A1.2 Brukeren trykker på ønsket lokasjon i kartet
A1.3 Appen viser en pin på valgt lokasjon
A1.4 Fortsetter fra steg 7 i hovedflyten

**A2: Klimadata er allerede cachet (alternativ til steg 8–10)**
A2.1 Appen oppdager at data for denne lokasjonen allerede finnes i Room DB
A2.2 Appen henter cachet data lokalt uten å gjøre API-kall
A2.3 Fortsetter fra steg 11 i hovedflyten

### Unntak
U1: Ingen internettforbindelse
- Appen varsler brukeren om manglende tilkobling
- Dersom lokasjonen er cachet tidligere, tilbys brukeren å se cachet data

U2: Frost API returnerer feil eller tomt svar
- Appen varsler brukeren om at klimadata ikke kunne hentes
- Brukeren kan forsøke igjen eller velge en annen lokasjon


## Use-case diagram
Dette diagrammet viser brukerens ulike handlinger når de skal hente historisk klima data for en valgt lokasjon:

<img src="Diagrams/Use_case_diagram_1.png" width="1376" height="1236"  alt="Use case diagram for use case 1"/>

Fargene i diagrammet angir hva Brukeren gjorde (grønn), muligheter (blå) og funksjoner som er tilgjengelig mens om ikke ble brukt i denne flyten (gul)

---
### Sekvensdiagram som illustrerer use case 1
Her er et sekvensdiagram som modellerer dataflyten til Frost API, med caching, alternativ flyt og spesialbehandlingen av solskinnsdata.
Dette viser hele dataflyten fra UI ned til API og tilbake, inkludert:

- **Stasjonscaching** i minnet (unngår gjentatte API-kall)
- **Room cache-first** logikken (treff vs miss)
- **Solskinn som spesialtilfelle** med `Note` for å forklare hvorfor
- **Alt-blokker** for alternativ flyt (cachet vs ikke cachet)

```mermaid
sequenceDiagram
participant UI as Skjerm (UI)
participant VM as FrostViewModel
participant Repo as FrostRepository
participant DB as Room (database)
participant API as Frost API

    UI->>VM: loadFrostStats(lokasjon)
    VM->>Repo: getTemperatureData(lat, lon)

    Repo->>Repo: getOrCacheStations(lat, lon)

    alt Stasjoner cachet i minnet
        Repo-->>Repo: returnerer cachet stasjonsliste
    else Ikke cachet
        Repo->>API: getStationsNearby(lat, lon)
        API-->>Repo: "SN1380,SN3290,SN3200"
        Repo->>Repo: lagrer i stationCache (minne)
    end

    Repo->>DB: getByKey(stationId = "SN1380")

    alt Cache-treff i Room
        DB-->>Repo: cachet klimadata
        Repo-->>VM: returnerer data umiddelbart
    else Cache-miss
        Repo->>API: getTemperatureNormals(lat, lon, sources)
        API-->>Repo: rådata (360 månedsverdier)
        Repo->>Repo: aggregateByMonthV0() — beregner 1991–2020 normaler
        Repo->>DB: insert(stationId, månedsverdier)
        Repo-->>VM: returnerer data
    end

    VM-->>UI: oppdaterer FrostUiState

    Note over Repo,API: Solskinn håndteres separat (kun 36 stasjoner i Norge)

    VM->>Repo: getSunshineData(lat, lon)
    Repo->>API: getSunshineStationNearby(lat, lon)
    API-->>Repo: nærmeste solskinnstasjon (f.eks. SN18700)
    Repo->>DB: getByKey(stationId = "SN18700")

    alt Cache-treff
        DB-->>Repo: cachet solskinndata
    else Cache-miss
        Repo->>API: getSunshineNormals(lat, lon, stationId)
        API-->>Repo: solskinndata
        Repo->>DB: insert(stationId, solskinndata)
    end

    Repo-->>VM: returnerer solskinndata
    VM-->>UI: oppdaterer FrostUiState (komplett)
```
---

## Use Case 2: Beregne og utforske GeoScore for en valgt lokasjon

### Mål
Brukeren skal kunne velge en lokasjon og beregne GeoScoren for den

### Aktører
Primær aktør: Bruker
Sekundær aktører: Geonorge adresse-API, Frost API (V1), Google maps composable og ArcGis NveZones API

### Betingelser
Prebetingelser:
- Brukeren er tilkoblet internett
- Brukeren har åpnet appen

Postbetingelser:
- GeoScoren er beregnet og vises på skjermen
- Scoren er cachet lokalt i Room DB for fremtidig bruk

### Hovedflyt
1. Brukeren åpner appen og ser hjemskjermen med et søkefelt
2. Brukeren trykker på søkefeltet og skriver inn ønsket adresse
3. Appen sender et API-kall til Geonorge sitt adresse-API
4. Geonorge returnerer en liste med adresseforslag
5. Brukeren velger en adresse fra listen
6. Appen viser den valgte adressen som en pin på kartet
7. Brukeren trykker på "Generer rapport" for den valgte lokasjonen
8. Appen navigerer brukeren til et loading screen
9. Appen sender API-kall til Frost V1 og ArcGis NveZones og henter nedbør,vind, flom og skred data for lokasjonen
10. Frost returnerer Nedbør og vind data for de siste 30 årene
11. ArcGis returnerer om stedet er i en flom sone eller en skred sone
12. Appen aggregerer Vind og nedbørs data og calculerer GeoScore samt Nedbørs-, Vind-, Skred- og Flom-Score
13. Appen setter disse alle scorene utenom GeoScore inn i en prompt som blir sendt til Chat GPT API
14. Chat GPT API-et returnerer en tekstilg beskrivelse med tiltak for de forskjellige scorene
15. Appen cacher scorene og beskrivelsene i Room DB
16. Appen viser scorene og beskrivelsene på skjermen
17. brukeren navigerer seg gjennom Raport skjermen og utforsker GeoScoren


### Alternativ flyt

**A1: GeoScoren er allerede cachet (alternativ til steg 8–10)**
A2.1 Appen oppdager at data for denne lokasjonen allerede finnes i Room DB
A2.2 Appen henter cachet data lokalt uten å gjøre API-kall
A2.3 Fortsetter fra steg 16 i hovedflyten

## Unntak
U1: Ingen internettforbindelse
- Appen varsler brukeren om manglende tilkobling
- Dersom lokasjonen er cachet tidligere, tilbys brukeren å se cachet data

U2: Frost API returnerer tomt svar
- Appen varsler brukeren om at det ikke finnes tilstrekkelig data for å beregne scoren


## Use-case diagram
Dette diagrammet viser brukerens ulike handlinger når de generere og utforske GeoScore data for en valgt lokasjon:

<img src="Diagrams/Use_case_diagram_2.png" width="1356" height="1306"  alt="Use case diagram for use case 2"/>

Fargene i diagrammet angir hva Brukeren gjorde (grønn), muligheter (blå) og funksjoner som er tilgjengelig men som ikke ble brukt i denne flyten (gul)

---
## Klassediagram

Klassediagrammet viser de relevante klassene og deres relasjoner for use casene.

```mermaid
---
config:
  layout: elk
---
classDiagram
    %% UI Layer

    class HomeScreen {
    }
    class SearchScreen {
        +onLocationSelected(location: Location)
    }
    class ClimateStatsScreen {
    }
    class GeoScoreScreen {
        +onSaveLocation()
    }

    HomeScreen "1" --> "0..1" SearchScreen : navigates to
    HomeScreen "1" --> "0..1" ClimateStatsScreen : navigates to
    HomeScreen "1" --> "0..1" GeoScoreScreen : navigates to

    %% ViewModel Layer

    class AppViewModel {
        +selectedLocation: StateFlow~Location?~
        +setSelectedArea(location: Location)
    }
    class SearchViewModel {
        +uiState: StateFlow~SearchUiState~
        +updateInput(text: String)
        +addRecentlySearched(location: Location)
    }
    class FrostViewModel {
        +uiState: StateFlow~FrostUiState~
        +loadFrostStats(location: Location)
    }
    class FrostUiState {
        +isLoading: Boolean
        +temperatureMean: List~Double~?
        +windMean: List~Double~?
        +sunshineHours: List~Double~?
        +snowMean: List~Double~?
        +precipitationMean: List~Double~?
    }
    class GeoScoreViewModel {
        +uiState: StateFlow~GeoScoreUiState~
        +load(location: Location)
    }
    class GeoScoreUiState {
        +isScoreLoading: Boolean
        +geoScore: GeoScore?
        +grade: String
        +scoreError: String?
        +isReportLoading: Boolean
        +aiReport: Report?
        +reportError: String?
    }
    class SavedViewModel {
        +saved: StateFlow~List~Location~~
        +addSaved(location: Location)
        +removeSaved(location: Location)
    }

    HomeScreen "1" o-- "1" AppViewModel : uses
    HomeScreen "1" o-- "1" FrostViewModel : uses
    ClimateStatsScreen "1" o-- "1" FrostViewModel : uses
    GeoScoreScreen "1" o-- "1" GeoScoreViewModel : uses
    GeoScoreScreen "1" o-- "1" SavedViewModel : uses
    SearchScreen "1" o-- "1" SearchViewModel : uses

    FrostViewModel "1" --> "1" FrostUiState : manages
    GeoScoreViewModel "1" --> "1" GeoScoreUiState : manages

    %% Domain Model

    class Location {
        +address: String
        +name: String
        +municipality: String?
        +county: String?
        +lat: Double
        +lon: Double
        +savedAt: Long
    }
    class GeoScore {
        +locationKey: String
        +geoScore: Double?
        +hazardScore: Double?
        +exposureScore: Double?
        +vulnerabilityScore: Double?
        +precipitationScore: Double?
        +windScore: Double?
        +floodScore: Double
        +landslideScore: Double
        +extremeWeatherDaysCount: Int?
    }
    class Report {
        +locationKey: String
        +extremePrecipitationText: String?
        +extremeWindText: String?
        +floodText: String?
        +landslideText: String?
    }

    AppViewModel "1" --> "0..1" Location : manages
    SearchViewModel "1" --> "*" Location : returns
    FrostViewModel "1" --> "1" Location : receives
    GeoScoreViewModel "1" --> "1" Location : receives
    GeoScoreViewModel "1" --> "0..1" GeoScore : manages
    GeoScoreViewModel "1" --> "0..1" Report : manages

    %% Use Case Layer

    class GetGeoScore {
        +calculateGeoScore(lat: Double, lon: Double): GeoScore
    }
    class GetHazardScore {
        +calculateHazardScore(lat: Double, lon: Double, observations): HazardScoreResult
    }
    class GetExposureScore {
        +calculateExposureScore(lat: Double, lon: Double, observations): ExposureScoreResult
    }
    class GetVulnerabilityScore {
        +calculateVulnerabilityScore(lat: Double, lon: Double): VulnerabilityScoreResult
    }
    class GetAiReport {
        +generateReport(geoScore: GeoScore): Report
    }

    GeoScoreViewModel "1" --> "1" GetGeoScore : depends on
    GeoScoreViewModel "1" --> "1" GetAiReport : depends on

    GetGeoScore "1" --> "1" GetHazardScore : uses
    GetGeoScore "1" --> "1" GetExposureScore : uses
    GetGeoScore "1" --> "1" GetVulnerabilityScore : uses

    %% Repository Layer

    class GeoSearchRepositoryService {
        <<interface>>
        +getSearchResults(query: String): SearchResult
    }
    class GeoSearchRepository {
        +getSearchResults(query: String): SearchResult
    }
    class FrostRepositoryService {
        <<interface>>
        +getTemperatureData(lat: Double, lon: Double): Result
        +getWindData(lat: Double, lon: Double): Result
        +getSunshineData(lat: Double, lon: Double): Result
        +getSnowData(lat: Double, lon: Double): Result
        +getPrecipitationData(lat: Double, lon: Double): Result
        +getWindAndPrecipitationObservations(lat: Double, lon: Double): WindAndPrecipitationObservationsResult
    }
    class FrostRepository {
        -stationCache: Map~String, String~
        -sunshineStationCache: Map~String, String~
        +getTemperatureData(lat: Double, lon: Double): Result
        +getWindData(lat: Double, lon: Double): Result
        +getSunshineData(lat: Double, lon: Double): Result
        +getSnowData(lat: Double, lon: Double): Result
        +getPrecipitationData(lat: Double, lon: Double): Result
        +getWindAndPrecipitationObservations(lat: Double, lon: Double): WindAndPrecipitationObservationsResult
    }
    class NveZonesRepositoryService {
        <<interface>>
        +isInFloodZone(lat: Double, lon: Double): Boolean
        +isInLandslideZone(lat: Double, lon: Double): Boolean
    }
    class NveZonesRepository {
        +isInFloodZone(lat: Double, lon: Double): Boolean
        +isInLandslideZone(lat: Double, lon: Double): Boolean
    }
    class ScoreCacheRepository {
        <<interface>>
        +getGeoScoreCache(locationKey: String): GeoScore?
        +saveGeoScore(geoScore: GeoScore)
        +getHazardCache(locationKey: String): HazardScoreResult?
        +saveHazardScore(locationKey: String, result: HazardScoreResult)
        +getExposureCache(locationKey: String): ExposureScoreResult?
        +saveExposureScore(locationKey: String, result: ExposureScoreResult)
        +getVulnerabilityCache(locationKey: String): VulnerabilityScoreResult?
        +saveVulnerabilityScore(locationKey: String, result: VulnerabilityScoreResult, isInFloodZone: Boolean, isInLandslideZone: Boolean)
    }
    class ScoreCacheRepositoryImpl {
        +getGeoScoreCache(locationKey: String): GeoScore?
        +saveGeoScore(geoScore: GeoScore)
        +getHazardCache(locationKey: String): HazardScoreResult?
        +saveHazardScore(locationKey: String, result: HazardScoreResult)
        +getExposureCache(locationKey: String): ExposureScoreResult?
        +saveExposureScore(locationKey: String, result: ExposureScoreResult)
        +getVulnerabilityCache(locationKey: String): VulnerabilityScoreResult?
        +saveVulnerabilityScore(locationKey: String, result: VulnerabilityScoreResult, isInFloodZone: Boolean, isInLandslideZone: Boolean)
    }
    class SavedRepository {
        <<interface>>
        +getAllSaved(): Flow~List~Location~~
        +addSaved(location: Location)
        +removeSaved(location: Location)
    }
    class SavedRepositoryImpl {
        +getAllSaved(): Flow~List~Location~~
        +addSaved(location: Location)
        +removeSaved(location: Location)
    }
    class ChatGPTRepository {
        <<interface>>
        +generateReport(geoScore: GeoScore): Report
    }
    class ChatGPTRepositoryImpl {
        +generateReport(geoScore: GeoScore): Report
    }

    GeoSearchRepository ..|> GeoSearchRepositoryService : implements
    FrostRepository ..|> FrostRepositoryService : implements
    NveZonesRepository ..|> NveZonesRepositoryService : implements
    ScoreCacheRepositoryImpl ..|> ScoreCacheRepository : implements
    SavedRepositoryImpl ..|> SavedRepository : implements
    ChatGPTRepositoryImpl ..|> ChatGPTRepository : implements

    SearchViewModel "1" --> "1" GeoSearchRepositoryService : depends on
    FrostViewModel "1" --> "1" FrostRepositoryService : depends on
    SavedViewModel "1" --> "1" SavedRepository : depends on
    GetGeoScore "1" --> "1" FrostRepositoryService : depends on
    GetGeoScore "1" --> "1" ScoreCacheRepository : depends on
    GetHazardScore "1" --> "1" ScoreCacheRepository : depends on
    GetExposureScore "1" --> "1" ScoreCacheRepository : depends on
    GetVulnerabilityScore "1" --> "1" NveZonesRepositoryService : depends on
    GetVulnerabilityScore "1" --> "1" ScoreCacheRepository : depends on
    GetAiReport "1" --> "1" ChatGPTRepository : depends on

    %% Data Source Layer

    class FrostDataSourceService {
        <<interface>>
        +getStationsNearby(lat: Double, lon: Double): String
        +getSunshineStationNearby(lat: Double, lon: Double): String
        +getTemperatureNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getWindHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getSunshineNormals(lat: Double, lon: Double, stationId: String): SunshineRawResult
        +getSnowDepthHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getPrecipitationNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getPrecipitationHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getRankedObservationsForWind(lat: Double, lon: Double): FrostV1ResponseDto
        +getRankedObservationsForPrecipitation(lat: Double, lon: Double): FrostV1ResponseDto
    }
    class FrostDataSource {
        -httpClient: HttpClient
        +getStationsNearby(lat: Double, lon: Double): String
        +getSunshineStationNearby(lat: Double, lon: Double): String
        +getTemperatureNormals(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
        +getWindHistory(lat: Double, lon: Double, sources: String): FrostV0ObservationResponseDto
    }
    class AddressApiService {
        <<interface>>
        +searchAddress(query: String): AddressResponseWrapper
    }
    class AddressRemoteDataSource {
        -httpClient: HttpClient
        +searchAddress(query: String): AddressResponseWrapper
    }
    class NveZonesRemoteDataSource {
        -httpClient: HttpClient
        +getLandslideZoneData(lat: Double, lon: Double): ArcGisResponseDto
        +getFloodZoneData(lat: Double, lon: Double): ArcGisResponseDto
    }
    class ChatGPTRemoteDataSource {
        +getReport(geoScore: GeoScore): Report
    }

    FrostDataSource ..|> FrostDataSourceService : implements
    AddressRemoteDataSource ..|> AddressApiService : implements

    FrostRepository "1" --> "1" FrostDataSourceService : depends on
    GeoSearchRepository "1" --> "1" AddressApiService : depends on
    NveZonesRepository "1" --> "1" NveZonesRemoteDataSource : depends on
    ChatGPTRepositoryImpl "1" --> "1" ChatGPTRemoteDataSource : depends on

    %% Database

    class AppDatabase {
        <<abstract>>
    }

    FrostRepository "1" *-- "1" AppDatabase : uses
    ScoreCacheRepositoryImpl "1" *-- "1" AppDatabase : uses
    SavedRepositoryImpl "1" *-- "1" AppDatabase : uses

```