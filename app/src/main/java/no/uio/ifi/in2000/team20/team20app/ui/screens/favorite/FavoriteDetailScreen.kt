package no.uio.ifi.in2000.team20.team20app.ui.screens.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostStats
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.AreaSummaryBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.GeomarkingInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HistoricalHighlightsGrid
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeHeaderSection
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    location: Location,
    onBackClick: () -> Unit,
    onOpenMap: () -> Unit,
    frostViewModel: FrostViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val frostUiState by frostViewModel.uiState.collectAsStateWithLifecycle()
    val isCurrentFavorite by favoritesViewModel.isCurrentFavorite.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
        favoritesViewModel.checkIfFavorite(location)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Favorittdetaljer")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HomeHeaderSection(
                    selectedLocation = location.name,
                    isFavorite = isCurrentFavorite,
                    onFavoriteClick = {
                        if (isCurrentFavorite) {
                            favoritesViewModel.removeFavorite(location)
                        } else {
                            favoritesViewModel.addFavorite(location)
                        }
                    }
                )
            }

            item {
                GeomarkingInfoBox(
                    selectedLocation = location.name,
                    geomarking = "C",
                    riskLabel = "Moderat georisiko",
                    expandedText = "Geomerkingen er basert på en samlet vurdering av historiske forhold i området. Dette kan inkludere terreng, nedbørsmønstre, lokal eksponering og andre forhold som påvirker naturfare over tid."
                )
            }

            item {
                AreaSummaryBox(
                    selectedLocation = location.name,
                    summary = "Dette området er lagret som favoritt slik at brukeren raskt kan få oversikt over historiske risikofaktorer, klimadata og videre utforske området i kartet."
                )
            }

            item {
                HistoricalHighlightsGrid(
                    selectedLocation = location.name,
                    averageTemperature = "5.8 °C",
                    precipitationLevel = "Høy",
                    terrainExposure = "Moderat",
                    floodRisk = "Lav–moderat"
                )
            }

            item {
                ExpandableInfoBox(
                    title = "Historiske klimadata"
                ) {
                    when {
                        frostUiState.isLoading -> {
                            Text("Laster data...")
                        }

                        frostUiState.error != null -> {
                            Text("Feil: ${frostUiState.error}")
                        }

                        frostUiState.frostStats != null -> {
                            FavoriteClimateInfoContent(frostStats = frostUiState.frostStats!!)
                        }

                        else -> {
                            Text("Ingen klimadata tilgjengelig for dette området.")
                        }
                    }
                }
            }

            item {
                ExpandableInfoBox(
                    title = "Risikomomenter"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Flomfare",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Området har indikasjoner på lav til moderat flomfare basert på historiske forhold og lokale terrengtrekk.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        Text(
                            text = "Skred og terreng",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Terrengforholdene tilsier moderat eksponering, og det kan være nyttig å undersøke høydeforskjeller, grunnforhold og avrenning nærmere.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        Text(
                            text = "Nedbør og klima",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Området kan være påvirket av perioder med høy nedbør, noe som kan øke belastningen på drenering og lokal overvannshåndtering.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // TODO: Change this to something visually more pleasing perhaps
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onOpenMap,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Åpne i kart")
                    }

                    Button(
                        onClick = {
                            favoritesViewModel.removeFavorite(location)
                            onBackClick()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fjern favoritt")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FavoriteClimateInfoContent(frostStats: FrostStats) {
    // TODO: Render monthly climate stats table (temperature, precipitation, snow, wind, sunshine)
    Text("Klimadata lastet – visning ikke implementert ennå.", style = MaterialTheme.typography.bodyMedium)
}