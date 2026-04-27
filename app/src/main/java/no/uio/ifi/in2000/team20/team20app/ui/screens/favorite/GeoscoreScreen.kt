package no.uio.ifi.in2000.team20.team20app.ui.screens.favorite

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.GeomarkingInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoscoreScreen(
    location: Location,
    onBackClick: () -> Unit,
    onHistoricDataClick: () -> Unit,
    frostViewModel: FrostViewModel,
    savedViewModel: SavedViewModel
) {
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
        savedViewModel.checkIfSaved(location)
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
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (isCurrentSaved) {
                                    savedViewModel.removeSaved(location)
                                } else {
                                    savedViewModel.addSaved(location)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isCurrentSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isCurrentSaved) "Fjern fra lagrede" else "Lagre"
                            )
                        }
                    }
                )

                PrimaryTabRow(selectedTabIndex = 0) {
                    Tab(
                        selected = true,
                        onClick = { },
                        text = { Text("Rapport") }
                    )
                    Tab(
                        selected = false,
                        onClick = onHistoricDataClick,
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
                GeomarkingInfoBox(
                    selectedLocation = location.name,
                    geomarking = "C",
                    riskLabel = "Moderat risiko",
                    expandedText = "Geomerkingen er basert på en samlet vurdering av historiske forhold i området. Dette kan inkludere terreng, nedbørsmønstre, lokal eksponering og andre forhold som påvirker naturfare over tid."
                )
            }

            item {
                ExpandableInfoBox(
                    title = "Risikofaktorer"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // TODO: Replace with real risk factor rows (Flom, Skred, Radon, with progress bars and grade badges
                        // Data source: Jurius algorithm
                        Text(
                            "Innhold kommer snart",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                ExpandableInfoBox(
                    title = "Detaljert analyse"
                ) {
                    // TODO: Add accordion cards per risk factor with expandable
                    // descriptions and bullet point data
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