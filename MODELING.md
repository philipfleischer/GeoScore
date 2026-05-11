# MODELING.md 

### Sekvensdiagram
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