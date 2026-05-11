package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.components.SectionCard
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.ui.theme.*
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.DotProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import ir.ehsannarmani.compose_charts.models.IndicatorCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimateStatsScreen(
    location: Location,
    onBackClick: () -> Unit,
    onRapportClick: () -> Unit,
    frostViewModel: FrostViewModel
) {
    val frostUiState by frostViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .semantics{
                        onClick(
                            label = "See geo-score report",
                            action = {
                                onRapportClick()
                                true
                            }
                        )
                    }
            ) {
                SecondaryTabRow(
                    selectedTabIndex = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Tab(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
                        selected = false,
                        onClick = onRapportClick,
                        text = { Text("Rapport") },

                    )
                    Tab(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
                        selected = true,
                        onClick = { },
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
        Box(modifier = Modifier.fillMaxSize()) {  // Why box?
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

                // Temperature
                item {
                    ChartSection(
                        title = "Temperatur",
                        description = "Dette diagrammet viser gjennomsnittlig temperatur per måned, basert på målinger fra 1991–2020. " +
                                "Snittkurven viser den typiske månedstemperaturen i perioden. " +
                                "Maks-kurven viser gjennomsnittet av dagens høyeste temperaturer i hver måned, " +
                                "og min-kurven viser gjennomsnittet av dagens laveste temperaturer. " +
                                "Til sammen gir dette et bilde av hvor varme dagene og hvor kalde nettene vanligvis er.",
                        chartHeight = 280.dp,
                        isLoading = frostUiState.isLoading,
                        errorMessage = frostUiState.temperatureError,
                        onRetry = { frostViewModel.loadFrostStats(location) }
                    ) {
                        if (frostUiState.temperatureMean != null) {
                            GenericLineChart(
                                data = remember(frostUiState.temperatureMean, frostUiState.temperatureMax, frostUiState.temperatureMin) {
                                    listOf(
                                        Line(
                                            label = "Min (°C)",
                                            values = frostUiState.temperatureMin ?: emptyList(),
                                            color = SolidColor(MayaBlue),
                                            firstGradientFillColor = Color.Transparent,
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                radius = 4.dp,
                                                strokeWidth = 2.dp,
                                                strokeColor = SolidColor(MayaBlue)
                                            )
                                        ),
                                        Line(
                                            label = "Snitt (°C)",
                                            values = frostUiState.temperatureMean ?: emptyList(),
                                            color = SolidColor(DustyBlue),
                                            firstGradientFillColor = DustyBlue.copy(alpha = .2f),
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                radius = 4.dp,
                                                strokeWidth = 2.dp,
                                                strokeColor = SolidColor(DustyBlue)
                                            )
                                        ),
                                        Line(
                                            label = "Maks (°C)",
                                            values = frostUiState.temperatureMax ?: emptyList(),
                                            color = SolidColor(Salmon),
                                            firstGradientFillColor = Color.Transparent,
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                radius = 4.dp,
                                                strokeWidth = 2.dp,
                                                strokeColor = SolidColor(Salmon)
                                            )
                                        )
                                    )
                                },
                                title = "Månedlig temperatur",
                                chartHeight = 280.dp,
                                minValue = -20.0,
                                maxValue = 30.0,
                                unitSuffix = "°C",
                                indicatorCount = IndicatorCount.StepBased(stepBy = 5.0),
                                showZeroLine = true,
                                zeroLineColor = TrafficRed
                            )
                        } else {
                            Text("Ingen klimadata tilgjengelig.")
                        }
                    }
                }

                // Wind
                item {
                    ChartSection(
                        title = "Vind",
                        description = "Gjennomsnittlig vindstyrke per måned viser hvor mye det vanligvis blåser i perioden. " +
                                "Høyeste målte middelvind per måned viser hvor kraftig og vedvarende vinden kan bli, " +
                                "typisk opp mot stormstyrke i de mest utsatte månedene. " +
                                "Høyeste målte vindkast per måned viser hvor kraftige de kortvarige kastene kan være, " +
                                "noe som er viktig for vurdering av for eksempel vindutsatt infrastruktur, skog og bygg.",
                        chartHeight = 200.dp,
                        isLoading = frostUiState.isLoading,
                        errorMessage = frostUiState.windError,
                        onRetry = { frostViewModel.loadFrostStats(location) }
                    ) {
                        if (frostUiState.windMean != null) {
                            GenericLineChart(
                                data = remember(frostUiState.windMean, frostUiState.windMaxSpeed, frostUiState.windMaxGust) {
                                    listOf(
                                        Line(
                                            label = "Middelvind",
                                            values = frostUiState.windMean ?: emptyList(),
                                            color = SolidColor(TrafficGreen),
                                            firstGradientFillColor = TrafficGreen.copy(alpha = .2f),
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                strokeColor = SolidColor(TrafficGreen)
                                            )
                                        ),
                                        Line(
                                            label = "Maks middelvind",
                                            values = frostUiState.windMaxSpeed ?: emptyList(),
                                            color = SolidColor(RoyalGold),
                                            firstGradientFillColor = Color.Transparent,
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                strokeColor = SolidColor(RoyalGold)
                                            )
                                        ),
                                        Line(
                                            label = "Maks vindkast",
                                            values = frostUiState.windMaxGust ?: emptyList(),
                                            color = SolidColor(TrafficRed),
                                            firstGradientFillColor = Color.Transparent,
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                strokeColor = SolidColor(TrafficRed)
                                            )
                                        )
                                    )
                                },
                                title = "Månedlig vindstyrke (m/s)",
                                chartHeight = 200.dp,
                                minValue = 0.0,
                                maxValue = 30.0,
                                unitSuffix = "m/s",
                                indicatorCount = IndicatorCount.StepBased(stepBy = 5.0)
                            )
                        } else {
                            Text("Ingen vinddata tilgjengelig.")
                        }
                    }
                }

                // Sunshine
                item {
                    ChartSection(
                        title = "Soltimer",
                        description = "Diagrammet viser hvor mange timer solen i gjennomsnitt skinner direkte på målestasjonen per dag i hver måned. " +
                                "Det handler om solskinn, ikke om hvor lenge det er lyst. " +
                                "Tallene er beregnet ut fra målinger i perioden 1991–2020.",
                        chartHeight = 200.dp,
                        isLoading = frostUiState.isLoading,
                        errorMessage = frostUiState.sunshineError,
                        onRetry = { frostViewModel.loadFrostStats(location) }
                    ) {
                        if (frostUiState.sunshineHours != null) {
                            GenericLineChart(
                                data = remember(frostUiState.sunshineHours) {
                                    val safeValues = (frostUiState.sunshineHours ?: emptyList()).take(12).map { maxOf(0.0, it) }
                                    listOf(
                                        Line(
                                            label = "Timer/dag",
                                            values = safeValues,
                                            color = SolidColor(RoyalGold),
                                            firstGradientFillColor = RoyalGold.copy(alpha = .3f),
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                strokeColor = SolidColor(RoyalGold)
                                            )
                                        )
                                    )
                                },
                                title = "Soltimer per dag",
                                chartHeight = 200.dp,
                                minValue = 0.0,
                                maxValue = 21.0,
                                unitSuffix = " timer",
                                indicatorCount = IndicatorCount.StepBased(stepBy = 3.0)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val distanceText = frostUiState.sunshineDistanceKm
                                ?.let { " (${it.toInt()} km unna)" } ?: ""
                            Text(
                                text = "Data fra ${frostUiState.sunshineStationName}$distanceText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text("Ingen data om soltimer tilgjengelig.")
                        }
                    }
                }

                // Rain
                item {
                    ChartSection(
                        title = "Nedbør",
                        description = "Nedbørsdager viser typisk antall dager per måned med minst 1,0 mm målbar nedbør. " +
                                "Merk at i vintermånedene kan nedbøren komme som snø. " +
                                "Høyeste daglige nedbør per måned viser hvor kraftige regn eller snøværet typisk er. " +
                                "Høye verdier indikerer økt risiko for styrtregn, overvann og lokale oversvømmelser.",
                        chartHeight = 200.dp,
                        isLoading = frostUiState.isLoading,
                        errorMessage = frostUiState.precipitationError,
                        onRetry = { frostViewModel.loadFrostStats(location) }
                    ) {
                        if (frostUiState.precipitationDays != null && frostUiState.precipitationMean != null) {
                            Column {
                                GenericLineChart(
                                    data = remember(frostUiState.precipitationDays) {
                                        listOf(
                                            Line(
                                                label = "Dager",
                                                values = (frostUiState.precipitationDays ?: emptyList()).take(12),
                                                color = SolidColor(MayaBlue),
                                                firstGradientFillColor = MayaBlue.copy(alpha = .2f),
                                                secondGradientFillColor = Color.Transparent,
                                                curvedEdges = false,
                                                dotProperties = DotProperties(
                                                    enabled = true,
                                                    color = SolidColor(White),
                                                    radius = 4.dp,
                                                    strokeWidth = 2.dp,
                                                    strokeColor = SolidColor(MayaBlue)
                                                )
                                            )
                                        )
                                    },
                                    title = "Nedbørsdager per måned",
                                    chartHeight = 200.dp,
                                    minValue = 0.0,
                                    maxValue = 21.0,
                                    unitSuffix = " dager",
                                    indicatorCount = IndicatorCount.StepBased(stepBy = 3.0)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                GenericLineChart(
                                    data = remember(frostUiState.precipitationMean) {
                                        listOf(
                                            Line(
                                                label = "Nedbør (mm)",
                                                values = (frostUiState.precipitationMean ?: emptyList()).take(12),
                                                color = SolidColor(TrafficGreen),
                                                firstGradientFillColor = TrafficGreen.copy(alpha = .2f),
                                                secondGradientFillColor = Color.Transparent,
                                                curvedEdges = false,
                                                dotProperties = DotProperties(
                                                    enabled = true,
                                                    color = SolidColor(White),
                                                    radius = 4.dp,
                                                    strokeWidth = 2.dp,
                                                    strokeColor = SolidColor(TrafficGreen)
                                                )
                                            )
                                        )
                                    },
                                    title = "Høyeste daglige nedbør per måned",
                                    chartHeight = 200.dp,
                                    minValue = 0.0,
                                    maxValue = 60.0,
                                    unitSuffix = "mm",
                                    indicatorCount = IndicatorCount.StepBased(stepBy = 10.0)
                                )
                            }
                        } else {
                            Text("Ingen nedbørsdata tilgjengelig.")
                        }
                    }
                }

                // Snow
                item {
                    ChartSection(
                        title = "Snø",
                        description = "Diagrammet viser gjennomsnittlig og høyeste målte snødybde per måned, basert på målinger fra 1991–2020. " +
                                "Høye verdier kan påvirke fremkommelighet, behov for snørydding og belastning på tak.",
                        chartHeight = 240.dp,
                        isLoading = frostUiState.isLoading,
                        errorMessage = frostUiState.snowError,
                        onRetry = { frostViewModel.loadFrostStats(location) }
                    ) {
                        if (frostUiState.snowMean != null && frostUiState.snowMax != null) {
                            GenericLineChart(
                                data = remember(frostUiState.snowMean, frostUiState.snowMax) {
                                    listOf(
                                        Line(
                                            label = "Snittdybde (cm)",
                                            values = frostUiState.snowMean ?: emptyList(),
                                            color = SolidColor(MayaBlue),
                                            firstGradientFillColor = MayaBlue.copy(alpha = .2f),
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                radius = 4.dp,
                                                strokeWidth = 2.dp,
                                                strokeColor = SolidColor(MayaBlue)
                                            )
                                        ),
                                        Line(
                                            label = "Maksdybde (cm)",
                                            values = frostUiState.snowMax ?: emptyList(),
                                            color = SolidColor(DarkBlue),
                                            firstGradientFillColor = Color.Transparent,
                                            secondGradientFillColor = Color.Transparent,
                                            curvedEdges = false,
                                            dotProperties = DotProperties(
                                                enabled = true,
                                                color = SolidColor(White),
                                                radius = 4.dp,
                                                strokeWidth = 2.dp,
                                                strokeColor = SolidColor(DarkBlue)
                                            )
                                        )
                                    )
                                },
                                title = "Snødybde per måned",
                                chartHeight = 240.dp,
                                minValue = 0.0,
                                maxValue = 120.0,
                                unitSuffix = " cm",
                                indicatorCount = IndicatorCount.StepBased(stepBy = 20.0)
                            )
                        } else {
                            Text("Ingen snødata tilgjengelig.")
                        }
                    }
                }
            }
        }
    }
}

// Reusable chart section with loading/error/loaded states
@Composable
private fun ChartSection(
    title: String,
    description: String,
    chartHeight: Dp,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    SectionCard(
        title = title,
        cardColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> ChartLoadingPlaceholder(height = chartHeight)
            errorMessage != null -> ChartErrorPlaceholder(
                height = chartHeight,
                message = friendlyErrorMessage(errorMessage),
                onRetry = onRetry
            )
            else -> content()
        }
    }
}

// Placeholders for loading and error state
@Composable
private fun ChartLoadingPlaceholder(height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Laster klimadata...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChartErrorPlaceholder(
    height: Dp,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        ErrorState(
            message = message,
            onRetry = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun friendlyErrorMessage(raw: String?): String = when {
    raw == null -> "Noe gikk galt. Prøv igjen senere."
    raw.contains("UnknownHostException") ||
    raw.contains("Unable to resolve host") -> "Ingen internettforbindelse. Sjekk tilkoblingen din."
    raw.contains("SocketTimeoutException") ||
    raw.contains("timed out", ignoreCase = true) -> "Forespørselen tok for lang tid. Prøv igjen."
    raw.contains("Frost 401") -> "Mangler tilgang til klimadataene."
    raw.contains("Frost 404") -> "Klimadata ikke funnet for dette stedet."
    raw.contains("Frost 412") ||
    raw.contains("No V0 stations found") ||
    raw.contains("No stations found") ||
    raw.contains("No nearby stations") -> "Ingen klimastasjoner funnet nær dette stedet."
    else -> "Noe gikk galt. Prøv igjen senere."
}

