# Naturhendelser – Risk Map Application

## IN2000 – Software Engineering Project

### University of Oslo – Department of Informatics

This project was developed as part of the course IN2000 Software Engineering with Project Work at the University of Oslo.

The application focuses on visualizing natural hazard risks for homeowners and property developers.
It allows users to explore geographic areas and understand potential risks related to natural events such as flooding, landslides, and extreme weather.


### Table of Contents
- Project Overview￼
- Team￼
- Application Features￼
- Architecture￼
- Technologies￼
- API Usage￼
- Installation￼
- Testing￼
- Project Documentation￼



### Project Overview

Natural hazards represent an increasing challenge for urban planning and housing development.
This application aims to make risk information accessible and understandable through an interactive mobile interface.

The app allows users to:
- Explore geographic areas on an interactive map
- View natural hazard risk indicators
- Assess probability and severity of hazards
- Save locations
- Support better decision-making for housing and development

The concept is inspired by the energy labeling system used for apartments and houses, where each area receives a risk classification based on natural hazards.


### Team

Team 20 – IN2000
- Veronica Corsepius Melen - veroncm@uio.no
- Matilda Wold Dahl - matildad@uio.no
- Jeryos Abdo - jeryosa@uio.no
- Victoria Reinseth-Tollefsen - victre@uio.no
- David Hovde – davidhov@uio.no
- Philip Elias Fleischer – philipef@uio.no



### Application Features

The application includes the following main features:

- Home Screen with overview details of selected locations.
- Users can explore natural hazard risks across Norway through an interactive map.
- Saved Locations Screen.

Map features include:
- Zoom and navigation
- Visual hazard overlays
- Area risk classification



### Area Risk Overview

For each selected location, the app displays:
- Flood risk
- Landslide risk
- Wind and storm exposure
- Overall hazard rating
- Recommendations for housing and construction



### Saved locations

Users can save locations to quickly revisit important areas.



### Screens

The application currently includes the following screens:
- HomeScreen
  Overview and quick access to risk information
- MapScreen
  Interactive exploration of hazard data
- AreaDetailScreen
  Detailed hazard analysis for a selected location
- SavedScreen
  Saved locations for quick access
- SettingsScreen
  Push notifications, light/dark theme, project information, and more.


### Architecture

The application follows a Model–View–ViewModel (MVVM) architecture.

UI (Jetpack Compose) -> ViewModel -> Repository -> API Layer -> MET APIs

This structure ensures:
- separation of concerns
- improved testability
- maintainable code structure

The project structure is organized as:

- app
    - data
        - api
        - model
        - repository
    - domain
        - model
    - ui
        - screens
        - components
    - viewmodel


### Technologies

The application is built using modern Android development tools.

Technologies and Purposes:
- Kotlin: Main programming language
- Jetpack Compose: UI framework
- Material 3: UI components
- MVVM: Application architecture
- Ktor Client: Network requests
- Kotlinx Serialization: JSON parsing
- MockK	Unit: testing
- JUnit: Testing framework
- Google Maps: Map rendering


### API Usage

The application retrieves environmental data from APIs provided by the Norwegian Meteorological Institute (MET).

Examples include:
- Locationforecast API
- MetAlerts API

These APIs provide:
- environmental indicators
- weather forecasts
- hazard warnings


### Installation

Requirements
- Android Studio
- Internet connection
- Minimum SDK: 24
- Recommended SDK: 34 or higher


### Clone the repository

git clone https://github.uio.no/IN2000-V26/team-20.git

cd team-20



### Open the project
	1.	Open Android Studio
	2.	Select Open Project
	3.	Choose the project directory
	4.	Wait for Gradle Sync to complete


### Run the application
	1.	Open Device Manager
	2.	Create an Android emulator
	3.	Recommended device: Pixel 5
	4.	Recommended API level: 34+
	5.	Press Run ▶


### Security and Privacy

Security and privacy were considered important design principles throughout the project. Although the application is educational, it handles geographic data, therefore it benefits from a cautious and privacy-aware design.

The application follows a manual-search-first approach. Instead of relying on automatic access to the user’s physical location, the app is designed around areas actively selected by the user. This supports privacy by design and gives the user clear control over what location data is used.

The project also follows the principle of least privilege. The application should only access the data and permissions necessary for its core functionality. By avoiding unnecessary location access by default, the app reduces both privacy risk and unnecessary system exposure.

Basic input validation is used in the search flow to make the system more robust and predictable. User input is validated before it is sent to external APIs, which improves reliability and reduces unnecessary or malformed requests.

Together, these choices reflect a practical secure-by-default mindset. They make the application more professional, easier to reason about, and better aligned with responsible software engineering practices.

### Testing

The project includes unit tests written using Test Driven Development (TDD).

Tests are implemented for:
- ViewModel
- Repository
- API layer

The following tools are used:
- JUnit
- MockK
- Ktor MockEngine
- kotlinx-coroutines-test

Tests can be run using:

./gradlew test


### Project Documentation

Additional documentation is included in the repository:

File: Description
- README.md: Project overview
- ARCHITECTURE.md: Architecture explanation
- MODELING.md: System diagrams
- PROCESS.md: Development process


### Conclusion

This project demonstrates how software engineering principles such as clean architecture, test driven development, and modular design can be applied to build a modern Android application.

The goal of the project is to make natural hazard information accessible and understandable for people involved in housing and urban development.


### Screenshots


### License

This project was developed for educational purposes as part of the IN2000 Software Engineering course at the University of Oslo.
