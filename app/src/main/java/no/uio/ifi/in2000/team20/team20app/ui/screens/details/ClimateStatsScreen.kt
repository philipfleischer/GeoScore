package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.FrostWindNormal
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ClimateInfoContent
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                SecondaryTabRow(selectedTabIndex = 1, modifier = Modifier.fillMaxWidth()) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Lurer du på hvordan klimaet er for ${location.name}? Her kan du få et overblikk.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                )
            }

            // Temperature
            item {
                ExpandableInfoBox(
                    title = "Temperatur",
                    initiallyExpanded = true
                ) {
                    Text(
                        text = "Dette diagrammet viser gjennomsnittlig temperatur. Klikk for å lese mer.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.error != null -> {
                            Text("Feil: ${frostUiState.error}")
                        }

                        frostUiState.frostStats != null -> {
                            ClimateInfoContent(frostStats = frostUiState.frostStats!!)
                        }

                        else -> {
                            Text("Ingen klimadata tilgjengelig.")
                        }
                    }
                }
            }

            // Snow
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

                // Rain
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

                // Wind
                item {
                    ExpandableInfoBox(
                        title = "Vind",
                        initiallyExpanded = false
                    ) {
                        Text(
                            text = "Dette diagrammet viser gjennomsnittlig vindstyrke per måned.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        when {
                            frostUiState.isLoading -> {
                                Text("Laster klimadata...")
                            }

                            frostUiState.error != null -> {
                                Text("Feil: ${frostUiState.error}")
                            }

                            frostUiState.frostStats?.wind != null -> {
                                WindChart(windNormals = frostUiState.frostStats!!.wind!!)
                            }

                            else -> {
                                Text("Ingen vinddata tilgjengelig.")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Compose chart for temperature
@Composable
fun TemperatureChart(
    monthlyTemperatures: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = "Månedlig temperatur",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            data = remember(monthlyTemperatures) {
                listOf(
                    Line(
                        label = "Temperatur (°C)",
                        values = monthlyTemperatures,
                        color = SolidColor(Color(0xFF378ADD)),
                        firstGradientFillColor = Color(0xFF378ADD).copy(alpha = .3f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            //strokeWidth = 4f,
                            //radius = 6f,
                            strokeColor = SolidColor(Color(0xFF378ADD))
                        )
                    )
                )
            },
            zeroLineProperties = ZeroLineProperties(
                enabled = true,
                color = SolidColor(Color.Red),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = false),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            minValue = -20.0,
            maxValue = 50.0,
        )
    }
}

@Composable
@Preview
fun TemperatureChartPreview() {
    TemperatureChart(
        monthlyTemperatures = listOf(-3.0, -2.5, 1.2, 6.4, 11.8, 15.9,
            17.2, 16.8, 12.1, 7.3, 2.1, -1.4)
    )
}

// Compose chart for wind speed
@Composable
fun WindChart(
    windNormals: Map<Int, FrostWindNormal>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

    val meanSpeeds  = (1..12).map { windNormals[it]?.mean     ?: 0.0 }
    val maxSpeeds   = (1..12).map { windNormals[it]?.maxSpeed ?: 0.0 }
    val maxGusts    = (1..12).map { windNormals[it]?.maxGust  ?: 0.0 }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = "Månedlig vindstyrke",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            data = remember(windNormals) {
                listOf(
                    Line(
                        label = "Middelvind (m/s)",
                        values = meanSpeeds,
                        color = SolidColor(Color(0xFF4CAF50)),
                        firstGradientFillColor = Color(0xFF4CAF50).copy(alpha = .2f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            strokeColor = SolidColor(Color(0xFF4CAF50))
                        )
                    ),
                    Line(
                        label = "Maks middelvind (m/s)",
                        values = maxSpeeds,
                        color = SolidColor(Color(0xFFFFA726)),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            strokeColor = SolidColor(Color(0xFFFFA726))
                        )
                    ),
                    Line(
                        label = "Maks vindkast (m/s)",
                        values = maxGusts,
                        color = SolidColor(Color(0xFFEF5350)),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            strokeColor = SolidColor(Color(0xFFEF5350))
                        )
                    )
                )
            },
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            minValue = 0.0,
            maxValue = 30.0,
        )
    }
}
