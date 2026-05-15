package no.uio.ifi.in2000.team20.team20app.ui.screens.result

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.domain.model.scoreToGrade
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.components.LoadingState
import no.uio.ifi.in2000.team20.team20app.ui.components.ExpandableInfoBox
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
                            savedViewModel.addSaved(location, geoState.geoScore?.geoScore)
                        }
                    },
                    onNavigateToMap = onNavigateToMap
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = "Utbedringene så vel som karakterene er ikke faglig garantert. "+
                            "All handling basert på denne informasjonen gjøres på eget ansvar."
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

    val nveTiltakLink = "https://veiledere.nve.no/sikringshandboka/moduler/modul-f1-300-mulige-tiltak-mot-flom-og-oversvommelse/"
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
        Column{
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

                Column{
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
                    modifier = Modifier.clickable { uriHandler.openUri(nveTiltakLink) }
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
                        modifier = Modifier.clickable { uriHandler.openUri(nveTiltakLink) }
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
                        modifier = Modifier.clickable { uriHandler.openUri(nveTiltakLink) }
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
                        modifier = Modifier.clickable { uriHandler.openUri(nveTiltakLink) }
                    )
                }
            }



        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeomarkingBadge(
    grade: String,
    modifier: Modifier = Modifier,
    iconStyle: Boolean = true,
    showTooltip: Boolean = false,
) {
    val badgeColor = when (grade.uppercase()) {
        "A" -> Color(0xFF4CAF50)
        "B" -> Color(0xFF8BC34A)
        "C" -> Color(0xFFFFC107)
        "D" -> Color(0xFFFFC56B)
        "E" -> Color(0xFFEFA066)
        "F" -> Color(0xFFE36C5C)
        "?" -> Color(0xFFFFC107)
        else -> Color(0xFFBDBDBD)
    }

    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            RichTooltip(
                title = { Text("Karakter ${grade.uppercase()}") }
            ) {
                Text("Merkingen er gitt utifra fra en skala fra A-F, der A betyr minst samlet risiko")
            }
        },
        state = tooltipState,
        enableUserInput = false
    ) {
        if (iconStyle) {
            Card(
                modifier = if (showTooltip) modifier.clickable { scope.launch { tooltipState.show() } } else modifier,
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = badgeColor)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grade.uppercase().ifEmpty { "?" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            Text(
                text = grade.uppercase().ifEmpty { "?" },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = badgeColor,
                modifier = if (showTooltip) modifier.clickable { scope.launch { tooltipState.show() } } else modifier
            )
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