package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ClimateInfoContent
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(location.name) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tilbake"
                            )
                        }
                    }
                )

                PrimaryTabRow(selectedTabIndex = 1) {
                    Tab(
                        selected = false,
                        onClick = onBackClick,
                        text = { Text("Rapport") }
                    )
                    Tab(
                        selected = true,
                        onClick = { },
                        text = { Text("Historisk klimadata") }
                    )
                }
            }
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
                when {
                    frostUiState.isLoading -> {
                        Text("Laster klimadata...")
                    }

                    frostUiState.error != null -> {
                        Text("Feil: ${frostUiState.error}")
                    }

                    frostUiState.frostStats != null -> {
                        ExpandableInfoBox(
                            title = "Temperatur",
                            initiallyExpanded = true
                        ) {
                            ClimateInfoContent(frostStats = frostUiState.frostStats!!)
                        }
                    }

                    else -> {
                        Text("Ingen klimadata tilgjengelig.")
                    }
                }
            }

            if (frostUiState.frostStats != null) {
                item {
                    ExpandableInfoBox(title = "Snø") {
                        // TODO: Add monthly snow depth chart
                        // Endpoint exists in FrostDataSource but data not yet fetched
                        Text(
                            "Innhold kommer snart",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    ExpandableInfoBox(title = "Nedbør") {
                        // TODO: Add monthly precipitation chart
                        // Endpoint exists in FrostDataSource but data not yet fetched
                        Text(
                            "Innhold kommer snart",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}