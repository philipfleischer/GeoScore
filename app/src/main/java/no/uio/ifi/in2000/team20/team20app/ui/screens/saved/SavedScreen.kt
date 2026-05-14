package no.uio.ifi.in2000.team20.team20app.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.GeoScoreViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.result.GeomarkingBadge
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.LARGE_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH
import no.uio.ifi.in2000.team20.team20app.util.Constants.SMALL_PADDING_DP
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

    // Box to ensure the whole screen has color, and the column get unified padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets(0))
    ){
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = if (compactScreenWidth) (DEFAULT_PADDING_DP * 3).dp else (DEFAULT_PADDING_DP * 2).dp,
                    start = if (compactScreenWidth) DEFAULT_PADDING_DP.dp else LARGE_PADDING_DP.dp,
                    end = if (compactScreenWidth) DEFAULT_PADDING_DP.dp else (DEFAULT_PADDING_DP * 5).dp,
                    bottom = if (compactScreenWidth) (DEFAULT_PADDING_DP * 2).dp else LARGE_PADDING_DP.dp
                )
            ,
            verticalArrangement = if (!saved.isEmpty()) Arrangement.spacedBy(DEFAULT_PADDING_DP.dp) else Arrangement.Center
        ) {
            if (saved.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(DEFAULT_PADDING_DP.dp)
                        .clip(shape = RoundedCornerShape(100))
                        .fillMaxHeight(0.1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f))
                    ,
                    contentAlignment = Alignment.Center
                ){
                    Text( // Could be in the center of the screen
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SMALL_PADDING_DP.dp),
                        text = "Når du har lagret et sted, vil det vises her.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                Text(
                    text = "Lagrede steder",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
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
            },
            location = location.name
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
                        label = "Slett lagret adresse ${location.name}",
                        action = {
                            showDeleteDialog.value = true
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = "Åpne georapport for lagret adresse ${location.name}",
                        action = {
                            onOpenReport()
                            true
                        }
                    ),
                )
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
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
            GeomarkingBadge(
                grade = geoState.grade,
                iconStyle = false,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .semantics { isTraversalGroup = true }
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
                .clearAndSetSemantics {},

            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onOpenReport,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Åpne rapport", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

private fun formatSavedAt(savedAt: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return "Sist generert: ${formatter.format(Date(savedAt))}"
}

//@Composable
//private fun GradeBadge(grade: String) {
//    val gradeColor = when (grade) {
//        "A" -> Color(0xFF4CAF50)
//        "B" -> Color(0xFF8BC34A)
//        "C" -> Color(0xFFFFC107)
//        "D" -> Color(0xFFFFC56B)
//        "E" -> Color(0xFFEFA066)
//        "F" -> Color(0xFFE36C5C)
//        else -> Color(0xFFBDBDBD)
//    }
//
//    Text(
//        text = grade.ifEmpty { "?" },
//        style = MaterialTheme.typography.headlineSmall,
//        fontWeight = FontWeight.Bold,
//        color = gradeColor,
//        fontSize = 28.sp
//    )
//}

@Composable
private fun DeleteLocationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    location: String
){
    AlertDialog(
        modifier = Modifier.semantics(mergeDescendants = true){
            liveRegion = LiveRegionMode.Polite
            customActions = listOf(
                CustomAccessibilityAction(
                    label = "Bekreft for å slette lagret adresse $location",
                    action = {
                        onConfirm()
                        true
                    }
                ),
                CustomAccessibilityAction(
                    label = "Avbryt å slette lagret adresse $location",
                    action = {
                        onDismiss()
                        true
                    }
                )
            ) },
        onDismissRequest = onDismiss,
        title = { Text("Fjern lokasjon") },
        text = { Text("Er du sikker på at du vil fjerne denne lokasjonen?")},
        confirmButton = {
            TextButton(modifier = Modifier.clearAndSetSemantics{} ,onClick = onConfirm) {
                Text("Fjern", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(modifier = Modifier.clearAndSetSemantics{}, onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}
