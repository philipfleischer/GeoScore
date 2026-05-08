package no.uio.ifi.in2000.team20.team20app.ui.screens.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetAiReport
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetGeoScore
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.GeoScoreViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.LARGE_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel,
    savedViewModel: SavedViewModel,
    onSavedClick: (Location) -> Unit
) {
    val saved by savedViewModel.saved.collectAsStateWithLifecycle()
    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(MEDIUM_SCREEN_WIDTH)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = if (compactScreenWidth) (DEFAULT_PADDING_DP*3).dp else (DEFAULT_PADDING_DP*2).dp,
                start = if (compactScreenWidth) DEFAULT_PADDING_DP.dp else LARGE_PADDING_DP.dp,
                end = if (compactScreenWidth) DEFAULT_PADDING_DP.dp else (DEFAULT_PADDING_DP*5).dp,
                bottom = if(compactScreenWidth) (DEFAULT_PADDING_DP*2).dp else LARGE_PADDING_DP.dp
            ),
        verticalArrangement = Arrangement.spacedBy(DEFAULT_PADDING_DP.dp)
    ) {
        if (saved.isEmpty()) {
            Text(
                text = "Du har ingen lagrede steder enda.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Lagrede steder",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(saved) { area ->
                    val geoScoreViewModel: GeoScoreViewModel = hiltViewModel(key = area.address)
                    SavedLocationCard(
                        location = area,
                        geoScoreViewModel = geoScoreViewModel,
                        onOpenReport = {
                            sharedViewModel.setSelectedArea(area)
                            onSavedClick(area)
                        },
                        onSavedToggle = { isSaved ->
                            if (isSaved) {
                                savedViewModel.removeSaved(area)
                            } else {
                                savedViewModel.addSaved(area)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedLocationCard(
    location: Location,
    geoScoreViewModel: GeoScoreViewModel,
    onOpenReport: () -> Unit,
    onSavedToggle: (Boolean) -> Unit,
) {
    val geoState by geoScoreViewModel.uiState.collectAsStateWithLifecycle()

    val showDeleteDialog = remember {mutableStateOf(false)}

    if (showDeleteDialog.value) {
        DeleteLocationDialog(
            onDismiss = { showDeleteDialog.value = false },
            onConfirm = {
                onSavedToggle(true)
                showDeleteDialog.value = false
            }
        )
    }

    LaunchedEffect(location) {
        geoScoreViewModel.load(location)
    }

    // Saved geo-score result cards
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Delete saved address",
                        action = {
                            onSavedToggle(true)
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = "Open geoscore repport",
                        action = {
                            onOpenReport()
                            true
                        }
                    )
                )
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            GradeBadge(grade = geoState.grade)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .semantics{ isTraversalGroup = true }
                ,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = formatSavedAt(location.savedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // TODO: Add share functionality ;)
//                IconButton(
//                    onClick = { },
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = "Del",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Last ned",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog.value = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Fjern fra lagret",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clearAndSetSemantics{},

            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onOpenReport,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Åpne rapport")
            }
        }
    }
}

private fun formatSavedAt(savedAt: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return "Sist generert: ${formatter.format(Date(savedAt))}"
}

@Composable
private fun GradeBadge(grade: String) {
    val gradeColor = when (grade) {
        "A" -> Color(0xFF4CAF50)
        "B" -> Color(0xFF8BC34A)
        "C" -> Color(0xFFFFC107)
        "D" -> Color(0xFFFFC56B)
        "E" -> Color(0xFFEFA066)
        "F" -> Color(0xFFE36C5C)
        else -> Color(0xFFBDBDBD)
    }

    Text(
        text = grade.ifEmpty { "?" },
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = gradeColor,
        fontSize = 28.sp
    )
}

@Composable
private fun DeleteLocationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fjern lokasjon") },
        text = { Text("Er du sikker på at du vil fjerne denne lokasjonen?")},
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Fjern", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}
