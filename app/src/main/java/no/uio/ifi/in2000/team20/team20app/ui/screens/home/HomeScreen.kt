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
import androidx.compose.foundation.lazy.LazyColumn
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
import no.uio.ifi.in2000.team20.team20app.domain.model.ClimateData
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    sharedViewModel: AppViewModel = viewModel()
) {
    val location = sharedViewModel.selectedLocation
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        viewModel.loadClimateData(location)
    }

    val selectedLocation = location.name

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        item {
            SearchBarObject(onOpenSearch = onOpenSearch)
        }

        item {
            HomeHeaderSection(selectedLocation = selectedLocation)
        }


        item {
            GeomarkingInfoBox(
                selectedLocation = selectedLocation,
                geomarking = "C",
                riskLabel = "Moderat georisiko",
                expandedText = "Geomerkingen er basert på en samlet vurdering av historiske forhold i området. " +
                        "Dette kan inkludere terreng, nedbørsmønstre, lokal eksponering og andre faktorer som påvirker naturfare over tid."
            )
        }

        item {
            AreaSummaryBox(
                selectedLocation = selectedLocation,
                summary = "Dette området har moderate historiske risikofaktorer knyttet til naturhendelser. " +
                        "Informasjonen er ment å gi brukeren en enkel og forståelig oversikt før videre utforsking i kart og detaljvisninger."
            )
        }

        item {
            HistoricalHighlightsGrid(
                selectedLocation = selectedLocation,
                averageTemperature = "5.8 °C",
                precipitationLevel = "Høy",
                terrainExposure = "Moderat",
                floodRisk = "Lav–moderat"
            )
        }

        item {
            ExpandableInfoBox(
                title = "Historiske klimadata"
            ) {
                when {
                    uiState.isLoading -> {
                        Text("Laster data...")
                    }
                    uiState.error != null -> {
                        Text("Feil: ${uiState.error}")
                    }
                    uiState.climateData != null -> {
                        ClimateInfoContent(climateData = uiState.climateData!!)
                    }
                    else -> {
                        Text("Søk et område for å vise historiske data.")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeaderSection(
    selectedLocation: String,
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

@Composable
private fun ClimateInfoContent(climateData: ClimateData) {
    Text(
        text = climateData.stationName,
        style = MaterialTheme.typography.bodyMedium
    )
    Text(
        text = climateData.stationId,
        style = MaterialTheme.typography.bodySmall
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Måned", style = MaterialTheme.typography.labelSmall)
        Text("Temp", style = MaterialTheme.typography.labelSmall)
        Text("Nedbør", style = MaterialTheme.typography.labelSmall)
    }

    Spacer(modifier = Modifier.height(4.dp))

    climateData.observations.takeLast(3).forEach { obs ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(obs.time.take(7), style = MaterialTheme.typography.bodyMedium)
            Text(
                obs.airTemperature?.let { "%.1f °C".format(it) } ?: "-",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                obs.precipitation?.let { "%.1f mm".format(it) } ?: "-",
                style = MaterialTheme.typography.bodyMedium
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