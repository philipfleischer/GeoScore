package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.IconButton
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    sharedViewModel: AppViewModel = viewModel(),
    savedViewModel: SavedViewModel,
    frostViewModel: FrostViewModel
) {
    val location = sharedViewModel.selectedLocation
    val frostUiState by frostViewModel.uiState.collectAsStateWithLifecycle()
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()

    // Calculates window width and returns true if the size width class is compact, and false for everything else.
    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    LaunchedEffect(location) {
        location?.let {
            frostViewModel.loadFrostStats(it)
            savedViewModel.checkIfSaved(it)
        }
    }

    val selectedLocation = location?.name ?: "Oslo"

    LazyVerticalGrid(
        columns = GridCells.Fixed(if(compactScreenWidth) 1 else 2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        //TODO: Update InfoBox component (rename to WelcomeInfoBox) so it fit current design
        //TODO: Remove hardcoded info box section and replace with updated InfoBox component
        item {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(if (compactScreenWidth) 1f else 0.5f)
            ){
                Text(
                    text = "Vit hva du kjøper- før du kjøper det",
                    fontSize = 40.sp,
                    lineHeight = 48.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics{ heading() }
                )
                Text(
                    text = "Få innsikt i grunnforhold og naturfare. Søk op en adresse og få en risikovurdering" +
                            "basert på geologisk og meterologisk data.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                )
            }
        }
        item {
            SearchBarObject(onOpenSearch = onOpenSearch)
        }
    }
}

@Composable
fun HomeHeaderSection(
    selectedLocation: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Naturfareoversikt",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Valgt område: $selectedLocation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.StarBorder
                        },
                        contentDescription = if (isFavorite) {
                            "Fjern fra favoritter"
                        } else {
                            "Legg til i favoritter"
                        },
                        tint = if (isFavorite) {
                            Color(0xFFFFC107)
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = "Se geomerking, historiske nøkkelfaktorer og klimadata for området.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun ExpandableInfoBox(
    title: String,
    modifier: Modifier = Modifier,
    rightContent: @Composable (() -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    cardColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (rightContent != null) {
                    rightContent()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun GeomarkingInfoBox(
    selectedLocation: String,
    geomarking: String,
    riskLabel: String,
    expandedText: String,
    modifier: Modifier = Modifier
) {
    ExpandableInfoBox(
        title = selectedLocation,
        modifier = modifier,
        cardColor = MaterialTheme.colorScheme.secondaryContainer,
        rightContent = {
            GeomarkingBadge(
                grade = geomarking
            )
        }
    ) {
        Text(
            text = "Samlet vurdering",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = riskLabel,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = expandedText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun GeomarkingBadge(
    grade: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (grade.uppercase()) {
        "A" -> Color(0xFFDFF5E1)
        "B" -> Color(0xFFBFE7A1)
        "C" -> Color(0xFFF1E38A)
        "D" -> Color(0xFFF3C56B)
        "E" -> Color(0xFFEFA066)
        "F" -> Color(0xFFE36C5C)
        "G" -> Color(0xFFB64545)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = badgeColor)
    ) {
        Text(
            text = grade.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AreaSummaryBox(
    selectedLocation: String,
    summary: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Områdeoversikt for $selectedLocation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Divider()

            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun HistoricalHighlightsGrid(
    selectedLocation: String,
    averageTemperature: String,
    precipitationLevel: String,
    terrainExposure: String,
    floodRisk: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Historiske nøkkelfaktorer for $selectedLocation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryMiniCard(
                title = "Temp.",
                value = averageTemperature,
                modifier = Modifier.weight(1f)
            )
            SummaryMiniCard(
                title = "Nedbør",
                value = precipitationLevel,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryMiniCard(
                title = "Terreng",
                value = terrainExposure,
                modifier = Modifier.weight(1f)
            )
            SummaryMiniCard(
                title = "Flomfare",
                value = floodRisk,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SummaryMiniCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.aspectRatio(1.9f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenLayoutPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //HomeHeaderSection()

            GeomarkingInfoBox(
                selectedLocation = "Oslo",
                geomarking = "C",
                riskLabel = "Moderat georisiko",
                expandedText = "Dette området har moderate historiske risikofaktorer knyttet til naturhendelser."
            )

            AreaSummaryBox(
                selectedLocation = "Oslo",
                summary = "Området har en moderat samlet historisk eksponering for naturfare."
            )

            HistoricalHighlightsGrid(
                averageTemperature = "5.8 °C",
                precipitationLevel = "Høy",
                terrainExposure = "Moderat",
                floodRisk = "Lav–moderat",
                selectedLocation = "Oslo",
                modifier = Modifier
            )
        }
    }
}

