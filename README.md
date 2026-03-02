# TEAM20APPLICATION

TEAM-20:
Philip
David
Jurius

### Versjonskontroll (Git & GitHub Workflow)
#### Denne seksjonen forklarer hvordan vi jobber med Git i prosjektet. Alle i gruppen skal følge denne arbeidsflyten.

1. Før du starter å jobbe, Hent siste versjon av main-branchen:
´´´
git checkout main
git pull origin main
´´´
Dette sikrer at du jobber på nyeste versjon.

#### 2. Lage en ny branch

Vi jobber ALDRI direkte på main.

Når du lager ny funksjonalitet (f.eks. navigation-bar):
´´´
git checkout -b feat/navigation-bar
´´´

Når du fikser en bug (f.eks. map crashing):
´´´
git checkout -b fix/crash-on-map
´´´

Regler:
- feat/ = ny funksjonalitet
- fix/ = bug-fix
- Bruk små bokstaver
- Bruk bindestrek mellom ord

#### 3. Lagre endringer (commit)
Se hva som er endret:
´´´
git status
´´´

Legg til alle filer:
´´´
git add .
´´´

Lag commit:
´´´
git commit -m "Add navigation bar to home screen"
´´´

Regler for commit-melding:
- Skriv på engelsk
- Start med stor forbokstav
- Beskriv hva som ble gjort
- Ikke skriv “stuff”, “changes”, “fix”

Mulige eksempler:

Add wind layer to map
Fix crash when location is null
Update UI for weather cards


⸻

#### 4. Push til GitHub

Første gang du pusher en ny branch:
´´´
git push -u origin feat/nav-bar
´´´

Etter første gang holder det med:
´´´
git push
´´´

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
´´´
git checkout main
git pull origin main
git checkout feat/nav-bar
git merge main
´´´

Hvis det kommer merge konflikt:
- Åpne filen
- Løs konflikten manuelt
- Lag commit igjen

⸻

#### 7. Slette branch etter merge

Etter PR er merged:
´´´
git checkout main
git pull origin main
git branch -d feat/nav-bar
´´´

For å slette den på GitHub:
´´´
git push origin --delete feat/nav-bar
´´´

⸻

### Hvis noe går galt

Se historikk:
´´´
git log
´´´

Angre siste commit (beholder endringer):
´´´
git reset --soft HEAD~1
´´´

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
