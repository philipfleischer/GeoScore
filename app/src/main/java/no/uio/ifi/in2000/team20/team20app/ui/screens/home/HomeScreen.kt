package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team20.team20app.ui.components.EmptyState
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.components.LoadingState
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun HomeScreen(
    areaName: String,
    latitude: Double,
    longitude: Double,
    onOpenMap: () -> Unit,
    onOpenDetails: () -> Unit,
    onOpenClimateStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    sharedViewModel: AppViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Laster data på nytt hver gang valgt område endres
    LaunchedEffect(areaName, latitude, longitude) {
        viewModel.loadArea(areaName, latitude, longitude)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header vises alltid, uavhengig av tilstand
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Naturhendelser",
                style = MaterialTheme.typography.headlineLarge
            )
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Åpne innstillinger"
                )
            }
        }

        // Søkefelt
        SearchBarObject(
            onOpenSearch = onOpenSearch,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Kortene vises alltid, tilstandsindikator er inline øverst
        HomeScreenContent(
            uiState = uiState,
            onOpenDetails = onOpenDetails,
            onOpenClimateStats = onOpenClimateStats
        )
    }
}


@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onOpenDetails: () -> Unit,
    onOpenClimateStats: () -> Unit
) {
    val theme = LocalTheme.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //TODO: Endre til nedtrekksmeny?
        item {
            when {
                uiState.isLoading          -> LoadingState(modifier = Modifier.fillMaxWidth())
                uiState.error != null      -> ErrorState(message = uiState.error, modifier = Modifier.fillMaxWidth())
                uiState.areaName.isEmpty() -> EmptyState(modifier = Modifier.fillMaxWidth())
                else                       -> Text("Valgt område: ${uiState.areaName}", style = MaterialTheme.typography.titleMedium)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Risikokort",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise sammendrag av risiko for området, f.eks: flom, skred og styrtregn.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Koble til ViewModel og hent API-data for det valgte området.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Anbefalte tiltak",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise klimatilpasningstiltak, som drenering.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Sett inn anbefalinger basert på område, hendelsestype og faglige kilder.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Barometer merkning",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise A-G for rangerings idéen.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Sett inn barometer energimerking ikonet eller hex diagrammet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Button(
                onClick = onOpenDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se områdedetaljer")
            }
        }

        item {
            Button(
                onClick = onOpenClimateStats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se klimastatistikk")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(
            uiState = HomeUiState(areaName = "Oslo", latitude = 59.9139, longitude = 10.7522),
            onOpenDetails = {},
            onOpenClimateStats = {}
        )
    }
}