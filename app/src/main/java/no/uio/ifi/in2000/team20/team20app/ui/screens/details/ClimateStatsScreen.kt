package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
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

                        frostUiState.temperatureError != null -> {
                            Text("Feil: ${frostUiState.temperatureError}")
                        }

                        frostUiState.temperatureMean != null -> {
                            TemperatureChart(
                                meanTemperatures = frostUiState.temperatureMean ?: emptyList(),
                                maxTemperatures = frostUiState.temperatureMax ?: emptyList(),
                                minTemperatures = frostUiState.temperatureMin ?: emptyList()
                            )
                            //kept for visual confirmation
//                            Spacer(modifier = Modifier.height(12.dp))
//                            ClimateInfoContent(
//                                temperatureMean = frostUiState.temperatureMean,
//                                temperatureMax = frostUiState.temperatureMax,
//                                temperatureMin = frostUiState.temperatureMin
//                            )
                        }

                        else -> {
                            Text("Ingen klimadata tilgjengelig.")
                        }
                    }
                }
            }

            // Snow
            item {
                ExpandableInfoBox(title = "Snø") {
                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.snowError != null -> {
                            Text("Feil: ${frostUiState.snowError}")
                        }

                        else -> {
                            // TODO: Add monthly snow depth chart
                            // Endpoint exists in FrostDataSource but data not yet fetched
                            Text(
                                "Innhold kommer snart",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Rain
            item {
                ExpandableInfoBox(title = "Nedbør") {
                    Text(
                        text = "Nedbørsdager viser typisk antall dager med målbar nedbør per måned. Høyeste daglige nedbør viser hvor kraftige regnværene typisk er — høye verdier indikerer risiko for styrtregn og oversvømmelse.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.precipitationError != null -> {
                            Text("Feil: ${frostUiState.precipitationError}")
                        }

                        frostUiState.precipitationDays != null && frostUiState.precipitationMean != null -> {
                            PrecipitationChart(
                                rainyDays = frostUiState.precipitationDays!!,
                                maxDailyPrecip = frostUiState.precipitationMean!!
                            )
                        }

                        else -> {
                            Text("Ingen nedbørsdata tilgjengelig.")
                        }
                    }
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

                        frostUiState.windError != null -> {
                            Text("Feil: ${frostUiState.windError}")
                        }

                        frostUiState.windMean != null -> {
                            WindChart(
                                windMean = frostUiState.windMean!!,
                                windMaxSpeed = frostUiState.windMaxSpeed!!,
                                windMaxGust = frostUiState.windMaxGust!!
                            )
                        }

                        else -> {
                            Text("Ingen vinddata tilgjengelig.")
                        }
                    }
                }
            }

            // Sunshine
            item {
                ExpandableInfoBox(
                    title = "Soltimer",
                    initiallyExpanded = false
                ) {
                    Text(
                        text = "Dette diagrammet viser gjennomsnittlig antall soltimer per dag per måned, basert på målinger fra 1991–2020.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.sunshineError != null -> {
                            Text("Feil: ${frostUiState.sunshineError}")
                        }

                        frostUiState.sunshineHours != null -> {
                            SunshineChart(sunshineHours = frostUiState.sunshineHours!!)
                            Spacer(modifier = Modifier.height(8.dp))
                            val distanceText = frostUiState.sunshineDistanceKm
                                ?.let { " (${it.toInt()} km unna)" } ?: ""
                            Text(
                                text = "Data fra ${frostUiState.sunshineStationName}$distanceText",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        else -> {
                            Text("Ingen soltimer data tilgjengelig.")
                        }
                    }
                }
            }
        }
    }
}

// Compose chart for temperature
//TODO make this prettier and more responsive
// Especially thinking of the bottom row and the grid. should line up better
@Composable
fun TemperatureChart(
    meanTemperatures: List<Double>,
    maxTemperatures: List<Double>,
    minTemperatures: List<Double>,
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
                .height(280.dp),
            data = remember(meanTemperatures, maxTemperatures, minTemperatures) {
                listOf(
                    Line(
                        label = "Min (°C)",
                        values = minTemperatures,
                        color = SolidColor(Color(0xFF378ADD)),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(Color(0xFF378ADD))
                        )
                    ),
                    Line(
                        label = "Snitt (°C)",
                        values = meanTemperatures,
                        color = SolidColor(Color(0xFF7F77DD)),
                        firstGradientFillColor = Color(0xFF7F77DD).copy(alpha = .2f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(Color(0xFF7F77DD))
                        )
                    ),
                    Line(
                        label = "Maks (°C)",
                        values = maxTemperatures,
                        color = SolidColor(Color(0xFFD85A30)),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(Color(0xFFD85A30))
                        )
                    )
                )
            },
            zeroLineProperties = ZeroLineProperties(
                enabled = true,
                color = SolidColor(Color.Red),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            minValue = -20.0,
            maxValue = 30.0,
        )
    }
}

@Composable
@Preview
fun TemperatureChartPreview() {
    TemperatureChart(
        meanTemperatures = listOf(-3.0, -2.5, 1.2, 6.4, 11.8, 15.9,
            17.2, 16.8, 12.1, 7.3, 2.1, -1.4),
        maxTemperatures = listOf(-2.1, -1.8, 2.9, 8.9, 15.5, 20.3,
            22.4, 21.3, 15.7, 10.0, 3.7, -0.4),
        minTemperatures = listOf(-10.3, -10.1, -5.8, -0.5, 5.4, 10.1,
            12.4, 11.8, 6.6, 2.0, -2.8, -7.9)
    )
}

// Compose chart for wind speed
@Composable
fun WindChart(
    windMean: List<Double>,
    windMaxSpeed: List<Double>,
    windMaxGust: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

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
            data = remember(windMean, windMaxSpeed, windMaxGust) {
                listOf(
                    Line(
                        label = "Middelvind (m/s)",
                        values = windMean,
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
                        values = windMaxSpeed,
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
                        values = windMaxGust,
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

@Composable
fun SunshineChart(
    sunshineHours: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = "Soltimer per dag",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            data = remember(sunshineHours) {
                listOf(
                    Line(
                        label = "Timer/dag",
                        values = sunshineHours,
                        color = SolidColor(Color(0xFFFFCA28)),
                        firstGradientFillColor = Color(0xFFFFCA28).copy(alpha = .3f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White),
                            strokeColor = SolidColor(Color(0xFFFFCA28))
                        )
                    )
                )
            },
            labelHelperProperties = LabelHelperProperties(enabled = false),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            minValue = 0.0,
            maxValue = 20.0,
        )
    }
}

// Keeping this for now for visual confirmation and peace of mind
@Composable
fun ClimateInfoContent(
    temperatureMean: List<Double>?,
    temperatureMax: List<Double>?,
    temperatureMin: List<Double>?
) {
    val months = listOf("Jan","Feb","Mar","Apr","Mai","Jun","Jul","Aug","Sep","Okt","Nov","Des")

    if (temperatureMean == null) {
        Text("Ingen temperaturdata tilgjengelig.", style = MaterialTheme.typography.bodyMedium)
        return
    }

    // Header row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Måned", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
        Text("Min", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
        Text("Snitt", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
        Text("Maks", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(4.dp))

    // One row per month
    months.forEachIndexed { i, month ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(month, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(temperatureMin?.getOrNull(i)?.let { "%.1f°".format(it) } ?: "-", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(temperatureMean.getOrNull(i)?.let { "%.1f°".format(it) } ?: "-", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(temperatureMax?.getOrNull(i)?.let { "%.1f°".format(it) } ?: "-", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PrecipitationChart(
    rainyDays: List<Double>,
    maxDailyPrecip: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")

    Column(modifier = modifier.fillMaxWidth()) {
        // Rainy days chart
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            Text(
                text = "Nedbørsdager per måned",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                data = remember(rainyDays) {
                    listOf(
                        Line(
                            label = "Dager",
                            values = rainyDays,
                            color = SolidColor(Color(0xFF378ADD)),
                            firstGradientFillColor = Color(0xFF378ADD).copy(alpha = .2f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = true,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(Color.White),
                                radius = 4.dp,
                                strokeWidth = 2.dp,
                                strokeColor = SolidColor(Color(0xFF378ADD))
                            )
                        )
                    )
                },
                labelHelperProperties = LabelHelperProperties(enabled = false),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months
                ),
                minValue = 0.0,
                maxValue = 20.0,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Max daily precipitation chart
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            Text(
                text = "Høyeste daglige nedbør per måned",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                data = remember(maxDailyPrecip) {
                    listOf(
                        Line(
                            label = "mm",
                            values = maxDailyPrecip,
                            color = SolidColor(Color(0xFF1D9E75)),
                            firstGradientFillColor = Color(0xFF1D9E75).copy(alpha = .2f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = true,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(Color.White),
                                radius = 4.dp,
                                strokeWidth = 2.dp,
                                strokeColor = SolidColor(Color(0xFF1D9E75))
                            )
                        )
                    )
                },
                labelHelperProperties = LabelHelperProperties(enabled = false),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months
                ),
                minValue = 0.0,
                maxValue = 60.0,
            )
        }
    }
}

