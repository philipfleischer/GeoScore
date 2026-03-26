package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import no.uio.ifi.in2000.team20.team20app.domain.model.ClimateData
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    sharedViewModel: AppViewModel = viewModel()
) {
    val location = sharedViewModel.selectedLocation // Location chosen by user
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Loads data on location changes
    LaunchedEffect(location) {
        viewModel.loadClimateData(location)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Seachfield and information about the area centered in the remaining space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SearchBarObject(onOpenSearch = onOpenSearch)

                // Climate data for choosen area
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Klima data fra valgt område",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        when {
                            uiState.isLoading -> {
                                Text("Laster data...")
                            }
                            uiState.error != null -> {
                                Text("Feil: ${uiState.error}")
                            }
                            uiState.climateData != null -> {
                                ClimateInfoContent(climateData = uiState.climateData!!) // We have already checked that it not null, this is therefor ok
                            }
                            else -> {
                                Text("Søk ett område for å vise noe her.")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClimateInfoContent(climateData: ClimateData) {
    Text(text = climateData.stationName, style = MaterialTheme.typography.bodyMedium)
    Text(text = climateData.stationId, style = MaterialTheme.typography.bodySmall)
    Spacer(modifier = Modifier.height(12.dp))
    // Header row
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Måned", style = MaterialTheme.typography.labelSmall)
        Text("Temp", style = MaterialTheme.typography.labelSmall)
        Text("Nedbør", style = MaterialTheme.typography.labelSmall)
    }
    Spacer(modifier = Modifier.height(4.dp))
    // Last 3 months
    climateData.observations.takeLast(3).forEach { obs ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // take(7) gets the first 7 characters of the string, year and month, e.g. 2023-01-01T00:00:00.000Z -> 2023-01
            Text(obs.time.take(7), style = MaterialTheme.typography.bodyMedium)
            Text(obs.airTemperature?.let { "%.1f °C".format(it) } ?: "-", style = MaterialTheme.typography.bodyMedium)
            Text(obs.precipitation?.let { "%.1f mm".format(it) } ?: "-", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onOpenSearch = {}
        )
    }
}