package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.scoreToGrade
import no.uio.ifi.in2000.team20.team20app.ui.components.LoadingState
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.GeomarkingBadge
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoscoreScreen(
    location: Location,
    onBackClick: () -> Unit,
    onHistoricDataClick: () -> Unit,
    frostViewModel: FrostViewModel,
    savedViewModel: SavedViewModel,
    geoScoreViewModel: GeoScoreViewModel
) {
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val geoState by geoScoreViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
        savedViewModel.checkIfSaved(location)
        geoScoreViewModel.load(location)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                SecondaryTabRow(selectedTabIndex = 0, modifier = Modifier.fillMaxWidth().semantics{isTraversalGroup = true}) {
                    Tab(
                        modifier = Modifier
                            .semantics{ onClick(label = "See geo-score rapport for ${location.name}", action = { true })},
                        selected = true,
                        onClick = { },
                        text = { Text("Rapport") }
                    )
                    Tab(
                        modifier = Modifier
                            .semantics{ onClick(
                                label = "See historic climate data for ${location.name}",
                                action = {
                                    onHistoricDataClick()
                                    true
                                })},
                        selected = false,
                        onClick = onHistoricDataClick,
                        text = { Text("Historisk klimadata") }
                    )
                }

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tilbake"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (geoState.isScoreLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "Beregner geoscore..."
            )
            return@Scaffold
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                GeomarkingCard(
                    location = location,
                    geoState = geoState,
                    isCurrentSaved = isCurrentSaved,
                    onSavedToggle = { saved ->
                        if (saved) {
                            savedViewModel.removeSaved(location)
                        } else {
                            savedViewModel.addSaved(location)
                        }
                    }
                )
            }

            item {
                Text(
                    text = "Detaljert analyse",
                    style = MaterialTheme.typography.titleLarge

                )
            }

            item {
                ExpandableInfoBox(
                    title = "☁\uFE0F Storm",

                    rightContent = {
                        GeomarkingBadge(
                            grade = geoState.geoScore?.let { scoreToGrade(it.windScore) } ?: "?"
                        )
                    }
                ) {
                    Text(
                        text = if (geoState.isReportLoading) "Laster rapport..."
                               else geoState.aiReport?.extremeWindText ?: "Chat kallet funket ikke",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                ExpandableInfoBox(
                    title = "⛰\uFE0F Skredfare",
                    rightContent = {
                        GeomarkingBadge(
                            grade = geoState.geoScore?.let { scoreToGrade(it.landslideScore) } ?: "?"
                        )
                    }
                ) {
                    Text(
                        text = if (geoState.isReportLoading) "Laster rapport..."
                               else geoState.aiReport?.landslideText ?: "Chat kallet funket ikke",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                ExpandableInfoBox(
                    title = "\uD83C\uDF0A Flomrisiko",
                    rightContent = {
                        GeomarkingBadge(
                            grade = geoState.geoScore?.let { scoreToGrade(it.floodScore) } ?: "?"
                        )
                    }
                ) {
                    Text(
                        text = if (geoState.isReportLoading) "Laster rapport..."
                               else geoState.aiReport?.floodText ?: "Chat kallet funket ikke",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                ExpandableInfoBox(
                    title = "\uD83C\uDF27\uFE0F Nedbør",
                    rightContent = {
                        GeomarkingBadge(
                            grade = geoState.geoScore?.let { scoreToGrade(it.precipitationScore) } ?: "?"
                        )
                    }
                ) {
                    Text(
                        text = if (geoState.isReportLoading) "Laster rapport..."
                               else geoState.aiReport?.extremePrecipitationText ?: "",
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

@Composable
private fun GeomarkingCard(
    location: Location,
    geoState: GeoScoreUiState,
    isCurrentSaved: Boolean,
    onSavedToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true){},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GeomarkingBadge(
                grade = geoState.grade.ifEmpty { "?" },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = gradeToRiskLabel(geoState.grade),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
                )
            }

            IconButton(
                modifier = Modifier.semantics {
                    onClick(
                        label = if (isCurrentSaved) "Remove address from saved" else "Save address",
                        action = {
                            onSavedToggle(isCurrentSaved)
                            true
                        })
                },
                onClick = { onSavedToggle(isCurrentSaved) }
            ) {
                Icon(
                    imageVector = if (isCurrentSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (isCurrentSaved) "Address saved" else "Address not saved"
                )
            }
        }
    }
}

private fun gradeToRiskLabel(grade: String): String = when (grade) {
    "A" -> "Svært lav risiko"
    "B" -> "Lav risiko"
    "C" -> "Moderat risiko"
    "D" -> "Høy risiko"
    "E" -> "Svært høy risiko"
    "F" -> "Kritisk risiko"
    else -> "Beregner..."
}