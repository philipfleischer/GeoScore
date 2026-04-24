package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.components.SharedTopAppBar
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel

@Composable
fun ClimateStatsScreen(
    location: Location,
    onBackClick: () -> Unit,
    frostViewModel: FrostViewModel
) {
    val frostUiState by frostViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
    }

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "Klimastatistikk",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                frostUiState.isLoading -> {
                    Text("Laster klimadata for ${location.name}...")
                }
                frostUiState.error != null -> {
                    Text("Feil: ${frostUiState.error}")
                }
                frostUiState.frostStats != null -> {
                    // TODO: Render full climate stats table using frostUiState.frostStats
                    Text("Klimadata lastet for ${location.name} – visning ikke implementert ennå.")
                }
                else -> {
                    Text("Ingen klimadata tilgjengelig.")
                }
            }
        }
    }
}