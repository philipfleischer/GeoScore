package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.scoreToGrade
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.components.LoadingState
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.GeomarkingBadge
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.SavedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoScoreScreen(
    location: Location,
    onBackClick: () -> Unit,
    onHistoricDataClick: () -> Unit,
    frostViewModel: FrostViewModel,
    savedViewModel: SavedViewModel,
    geoScoreViewModel: GeoScoreViewModel,
    onNavigateToMap: () -> Unit
) {
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val geoState by geoScoreViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(location) {

        Log.d("GeoScoreScreen", "LaunchedEffect called with $location")

        frostViewModel.loadFrostStats(location)
        savedViewModel.checkIfSaved(location)
        geoScoreViewModel.load(location)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                SecondaryTabRow(selectedTabIndex = 0, modifier = Modifier.fillMaxWidth().semantics{isTraversalGroup = true}) {
                    Tab(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .semantics{ onClick(label = "See geo-score rapport for ${location.name}", action = { true })},
                        selected = true,
                        onClick = { },
                        text = { Text("Rapport", color = MaterialTheme.colorScheme.secondary) }
                    )
                    Tab(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .semantics{ onClick(
                                label = "See historic climate data for ${location.name}",
                                action = {
                                    onHistoricDataClick()
                                    true
                                })},
                        selected = false,
                        onClick = onHistoricDataClick,
                        text = { Text("Historisk klimadata", color = MaterialTheme.colorScheme.secondary) }
                    )
                }

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tilbake",
                        tint = MaterialTheme.colorScheme.secondary
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
                message = listOf("Henter data fra koordinatene.","Henter data fra koordinatene..","Henter data fra koordinatene...", "Veier fordeler mot ulemper.", "Veier fordeler mot ulemper..","Veier fordeler mot ulemper...", "Nesten ferdig.", "Nesten ferdig..","Nesten ferdig...", "Er der straks.", "Er der straks..", "Er der straks...", "Formaterer resultatet.", "Formaterer resultatet..", "Formaterer resultatet...", "Alt er straks på plass.", "Alt er straks på plass..", "Alt er straks på plass...", "Siste ferdigstillinger.","Siste ferdigstillinger..", "Siste ferdigstillinger...")
            )
            return@Scaffold
        }
        if (geoState.scoreError != null) {
            ErrorState(
                message = "Ingen internettforbindelse. Sjekk nettverket ditt.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onRetry = { geoScoreViewModel.load(location) }
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),

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
                    },
                    onNavigateToMap = onNavigateToMap
                )
            }
        }
    }
}

@Composable
private fun GeomarkingCard(
    location: Location,
    geoState: GeoScoreUiState,
    isCurrentSaved: Boolean,
    onSavedToggle: (Boolean) -> Unit,
    onNavigateToMap: () -> Unit
) {

    val NVEtiltakLink = "https://veiledere.nve.no/sikringshandboka/moduler/modul-f1-300-mulige-tiltak-mot-flom-og-oversvommelse/"
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true){},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GeomarkingBadge( grade = geoState.grade.ifEmpty { "?" }, iconStyle = false, showTooltip = true)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = gradeToRiskLabel(geoState.grade),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }

                Column(){
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
                        ){
                        Icon(
                            imageVector = if (isCurrentSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isCurrentSaved) "Address saved" else "Address not saved",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        }

                    Text(
                        text = "vis i kart",
                        modifier = Modifier.clickable{onNavigateToMap()},
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableInfoBox(
                title = "☁\uFE0F Storm",

                rightContent = {
                    GeomarkingBadge(
                        grade = geoState.geoScore?.windScore?.let { scoreToGrade(it) } ?: "?"
                    )
                }
            ) {
                Text(
                    text = if (geoState.isReportLoading) "Laster rapport..."
                    else geoState.aiReport?.extremeWindText ?: "Chat kallet funket ikke",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!geoState.isReportLoading && geoState.aiReport != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )) { append("Les mer på NVE.no") }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { uriHandler.openUri(NVEtiltakLink) }
                )
            }
            }

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
                if (!geoState.isReportLoading && geoState.aiReport != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )) { append("Les mer på NVE.no") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { uriHandler.openUri(NVEtiltakLink) }
                    )
                }
            }

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
                if (!geoState.isReportLoading && geoState.aiReport != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )) { append("Les mer på NVE.no") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { uriHandler.openUri(NVEtiltakLink) }
                    )
                }
            }

            ExpandableInfoBox(
                title = "\uD83C\uDF27\uFE0F Nedbør",
                rightContent = {
                    GeomarkingBadge(
                        grade = geoState.geoScore?.precipitationScore?.let { scoreToGrade(it) } ?: "?"
                    )
                }
            ) {
                Text(
                    text = if (geoState.isReportLoading) "Laster rapport..."
                    else geoState.aiReport?.extremePrecipitationText ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!geoState.isReportLoading && geoState.aiReport != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )) { append("Les mer på NVE.no") }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { uriHandler.openUri(NVEtiltakLink) }
                    )
                }
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
    else -> "Ikke nok data..."
}