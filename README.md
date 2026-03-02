# TEAM20APPLICATION

TEAM-20:
Philip
David
Jurius
Veronica


## SSH KEYS
Her er en lenke for å generere SSH nøkler, så dere slipper å skrive inn brukernavn og passord til uiio brukeren deres hver gang dere pusher eller puller:
https://www.uio.no/english/services/it/home-away/ssh/help/keys.html

## Versjonskontroll (Git & GitHub Workflow)
### Denne seksjonen forklarer hvordan vi jobber med Git i prosjektet. Alle i gruppen skal følge denne arbeidsflyten.

1. Før du starter å jobbe, Hent siste versjon av main-branchen:
```
git checkout main
git pull origin main
```
Dette sikrer at du jobber på nyeste versjon.

#### 2. Lage en ny branch

Vi jobber ALDRI direkte på main.

Når du lager ny funksjonalitet (f.eks. navigation-bar):
```
git checkout -b feat/navigation-bar
```

Når du fikser en bug (f.eks. map crashing):
```
git checkout -b fix/crash-on-map
```

Regler:
- feat/ = ny funksjonalitet
- fix/ = bug-fix
- Bruk små bokstaver
- Bruk bindestrek mellom ord

#### 3. Lagre endringer (commit)
Se hva som er endret:
```
git status
```

Legg til alle filer:
```
git add .
```

Lag commit:
```
git commit -m "Add navigation bar to home screen"
```

Regler for commit-melding:
- Skriv på engelsk
- Start med stor forbokstav
- Beskriv hva som ble gjort
- Ikke skriv “stuff”, “changes”, “fix”

Mulige eksempler:
- Add wind layer to map
- Fix crash when location is null
- Update UI for weather cards


⸻

#### 4. Push til GitHub

Første gang du pusher en ny branch:
```
git push -u origin feat/nav-bar
```

Etter første gang holder det med:
```
git push
```

⸻

#### 5. Lage Pull Request (PR)
1. Gå til GitHub
2. Klikk “Compare & pull request”
3. Skriv kort beskrivelse av hva du har gjort
4. Velg minst én reviewer
5. Trykk “Create pull request”

Vi merger aldri våre egne PR uten review.

⸻

#### 6. Holde branchen din oppdatert

Hvis main har blitt oppdatert mens du jobber:
```
git checkout main
git pull origin main
git checkout feat/nav-bar
git merge main
```

Hvis det kommer merge konflikt:
- Åpne filen
- Løs konflikten manuelt
- Lag commit igjen

⸻

#### 7. Slette branch etter merge

Etter PR er merged:
```
git checkout main
git pull origin main
git branch -d feat/nav-bar
```

For å slette den på GitHub:
```
git push origin --delete feat/nav-bar
```

⸻

### Hvis noe går galt

Se historikk: 
```
git log
```

Angre siste commit (beholder endringer):
```
git reset --soft HEAD~1
```

⸻

### Kort oppsummert
1. git checkout main
2. git pull
3. git checkout -b feat/...
4. Jobb
5. git add .
6. git commit -m "..."
7. git push
8. Lag Pull Request



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
