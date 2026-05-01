package no.uio.ifi.in2000.team20.team20app.ui.screens.details

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.ExpandableInfoBox
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.ui.theme.*
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.TextStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimateStatsScreen(
    location: Location,
    onBackClick: () -> Unit,
    onRapportClick: () -> Unit,
    frostViewModel: FrostViewModel
) {
    val frostUiState by frostViewModel.uiState.collectAsStateWithLifecycle()
    val theme = LocalTheme.current

    LaunchedEffect(location) {
        frostViewModel.loadFrostStats(location)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                SecondaryTabRow(selectedTabIndex = 1, modifier = Modifier.fillMaxWidth()) {
                    Tab(
                        selected = false,
                        onClick = onRapportClick,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.background)
        ) {
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
                        tint = theme.tertiary,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = theme.tertiary
                    )
                }
            }

            // Temperature
            item {
                ExpandableInfoBox(
                    title = "Temperatur",
                    initiallyExpanded = true,
                    cardColor = theme.secondary
                ) {
                    Text(
                        text = "Dette diagrammet viser gjennomsnittlig temperatur per måned, basert på målinger fra 1991–2020. " +
                                "Snittkurven viser den typiske månedstemperaturen i perioden. " +
                                "Maks-kurven viser gjennomsnittet av dagens høyeste temperaturer i hver måned, " +
                                "og min-kurven viser gjennomsnittet av dagens laveste temperaturer. " +
                                "Til sammen gir dette et bilde av hvor varme dagene og hvor kalde nettene vanligvis er.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.temperatureError != null -> {
                            ErrorState(
                                message = friendlyErrorMessage(frostUiState.temperatureError),
                                onRetry = { frostViewModel.loadFrostStats(location) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        frostUiState.temperatureMean != null -> {
                            TemperatureChart(
                                meanTemperatures = frostUiState.temperatureMean ?: emptyList(),
                                maxTemperatures = frostUiState.temperatureMax ?: emptyList(),
                                minTemperatures = frostUiState.temperatureMin ?: emptyList()
                            )
                        }

                        else -> {
                            Text("Ingen klimadata tilgjengelig.")
                        }
                    }
                }
            }

            // Snow
            item {
                ExpandableInfoBox(title = "Snø", cardColor = theme.secondary) {
                    Text(
                        text = "Diagrammet viser gjennomsnittlig og høyeste målte snødybde per måned, basert på målinger fra 1991–2020. " +
                                "Høye verdier kan påvirke fremkommelighet, behov for snørydding og belastning på tak.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.snowError != null -> {
                            ErrorState(
                                message = friendlyErrorMessage(frostUiState.snowError),
                                onRetry = { frostViewModel.loadFrostStats(location) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        frostUiState.snowMean != null && frostUiState.snowMax != null -> {
                            SnowChart(
                                meanSnowDepth = frostUiState.snowMean!!,
                                maxSnowDepth = frostUiState.snowMax!!
                            )
                        }

                        else -> {
                            Text("Ingen snødata tilgjengelig.")
                        }
                    }
                }
            }

            // Rain
            item {
                ExpandableInfoBox(title = "Nedbør", cardColor = theme.secondary) {
                    Text(
                        text = "Nedbørsdager viser typisk antall dager per måned med minst 1,0 mm målbar nedbør. " +
                                "Merk at i vintermånedene kan nedbøren komme som snø. " +
                                "Høyeste daglige nedbør per måned viser hvor kraftige regn eller snøværet typisk er. " +
                                "Høye verdier indikerer økt risiko for styrtregn, overvann og lokale oversvømmelser.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.precipitationError != null -> {
                            ErrorState(
                                message = friendlyErrorMessage(frostUiState.precipitationError),
                                onRetry = { frostViewModel.loadFrostStats(location) },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                    initiallyExpanded = false,
                    cardColor = theme.secondary
                ) {
                    Text(
                        text = "Gjennomsnittlig vindstyrke per måned viser hvor mye det vanligvis blåser i perioden. " +
                                "Høyeste målte middelvind per måned viser hvor kraftig og vedvarende vinden kan bli, " +
                                "typisk opp mot stormstyrke i de mest utsatte månedene. " +
                                "Høyeste målte vindkast per måned viser hvor kraftige de kortvarige kastene kan være, " +
                                "noe som er viktig for vurdering av for eksempel vindutsatt infrastruktur, skog og bygg.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.windError != null -> {
                            ErrorState(
                                message = friendlyErrorMessage(frostUiState.windError),
                                onRetry = { frostViewModel.loadFrostStats(location) },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                    initiallyExpanded = false,
                    cardColor = theme.secondary
                ) {
                    Text(
                        text = "Diagrammet viser hvor mange timer solen i gjennomsnitt skinner direkte på målestasjonen per dag i hver måned. " +
                                "Det handler om solskinn, ikke om hvor lenge det er lyst. " +
                                "Tallene er beregnet ut fra målinger i perioden 1991–2020.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        frostUiState.isLoading -> {
                            Text("Laster klimadata...")
                        }

                        frostUiState.sunshineError != null -> {
                            ErrorState(
                                message = friendlyErrorMessage(frostUiState.sunshineError),
                                onRetry = { frostViewModel.loadFrostStats(location) },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                            Text("Ingen data om soltimer tilgjengelig.")
                        }
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
    val months = listOf("Jan", " ", " ", " ", " ", "Jun",
        " ", " ", " ", " ", " ", "Des")

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
                        color = SolidColor(MayaBlue),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(MayaBlue)
                        )
                    ),
                    Line(
                        label = "Snitt (°C)",
                        values = meanTemperatures,
                        color = SolidColor(DustyBlue),
                        firstGradientFillColor = DustyBlue.copy(alpha = .2f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(DustyBlue)
                        )
                    ),
                    Line(
                        label = "Maks (°C)",
                        values = maxTemperatures,
                        color = SolidColor(Salmon),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(Salmon)
                        )
                    )
                )
            },
            zeroLineProperties = ZeroLineProperties(
                enabled = true,
                color = SolidColor(TrafficRed),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            popupProperties = PopupProperties(
                enabled = true,
                animationSpec = tween(300),
                duration = 2000L,
                textStyle = TextStyle(
                    color = BrightWhite,
                    fontSize = 11.sp
                ),
                containerColor = Charcoal,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 8.dp,
                contentVerticalPadding = 4.dp,
                contentBuilder = { popup ->
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                    "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)}°C"
                }
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                // Temperature axis increases in steps by 5
                count = IndicatorCount.StepBased(stepBy = 5.0),
                contentBuilder = { value ->
                    "%.0f".format(value)
                }
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = AxisProperties(
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                ),
                // lineCount 12 for 12 months
                yAxisProperties = AxisProperties(
                    lineCount = 12,
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                )
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
    val months = listOf("Jan", " ", "Mar", " ", "Mai", " ",
        "Jul", " ", "Sep", " ", "Nov", " ")

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = "Månedlig vindstyrke (m/s)",
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
                        label = "Middelvind",
                        values = windMean,
                        color = SolidColor(TrafficGreen),
                        firstGradientFillColor = TrafficGreen.copy(alpha = .2f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            strokeColor = SolidColor(TrafficGreen)
                        )
                    ),
                    Line(
                        label = "Maks middelvind",
                        values = windMaxSpeed,
                        color = SolidColor(RoyalGold),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            strokeColor = SolidColor(RoyalGold)
                        )
                    ),
                    Line(
                        label = "Maks vindkast",
                        values = windMaxGust,
                        color = SolidColor(TrafficRed),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            strokeColor = SolidColor(TrafficRed)
                        )
                    )
                )
            },
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            popupProperties = PopupProperties(
                enabled = true,
                animationSpec = tween(300),
                duration = 2000L,
                textStyle = TextStyle(
                    color = BrightWhite,
                    fontSize = 11.sp
                ),
                containerColor = Charcoal,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 8.dp,
                contentVerticalPadding = 4.dp,
                contentBuilder = { popup ->
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                    "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)}m/s"
                }
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
    val months = listOf(" ", " ", "Mar", " ", " ", "Jun",
        " ", " ", "Sep", " ", " ", "Des")

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
                val safeValues = sunshineHours.map { maxOf(0.0, it) }
                listOf(
                    Line(
                        label = "Timer/dag",
                        values = safeValues,
                        color = SolidColor(RoyalGold),
                        firstGradientFillColor = RoyalGold.copy(alpha = .3f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            strokeColor = SolidColor(RoyalGold)
                        )
                    )
                )
            },
            labelHelperProperties = LabelHelperProperties(enabled = false),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            popupProperties = PopupProperties(
                enabled = true,
                mode = PopupProperties.Mode.PointMode(), // "snaps" to next month
                animationSpec = tween(300),
                duration = 2000L,
                textStyle = TextStyle(
                    color = BrightWhite,
                    fontSize = 11.sp
                ),
                containerColor = Charcoal,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 8.dp,
                contentVerticalPadding = 4.dp,
                contentBuilder = { popup ->
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                    "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)} dager"
                }
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                // Day axis increases in steps by 3 days
                count = IndicatorCount.StepBased(stepBy = 3.0),
                contentBuilder = { value ->
                    "%.0f".format(value)
                }
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = AxisProperties(
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                ),
                // lineCount 12 for 12 months
                yAxisProperties = AxisProperties(
                    lineCount = 12,
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                )
            ),
            minValue = 0.0,
            maxValue = 21.0,
        )
    }
}

@Composable
fun PrecipitationChart(
    rainyDays: List<Double>,
    maxDailyPrecip: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf(" ", "Feb", " ", " ", "Mai", " ",
        " ", "Aug", " ", " ", "Nov", " ")

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
                            color = SolidColor(MayaBlue),
                            firstGradientFillColor = MayaBlue.copy(alpha = .2f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = true,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(BrightWhite),
                                radius = 4.dp,
                                strokeWidth = 2.dp,
                                strokeColor = SolidColor(MayaBlue)
                            )
                        )
                    )
                },
                labelHelperProperties = LabelHelperProperties(enabled = false),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months
                ),
                popupProperties = PopupProperties(
                    enabled = true,
                    mode = PopupProperties.Mode.PointMode(), // "snaps" to next month
                    animationSpec = tween(300),
                    duration = 2000L,
                    textStyle = TextStyle(
                        color = BrightWhite,
                        fontSize = 11.sp
                    ),
                    containerColor = Charcoal,
                    cornerRadius = 8.dp,
                    contentHorizontalPadding = 8.dp,
                    contentVerticalPadding = 4.dp,
                    contentBuilder = { popup ->
                        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                            "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                        "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)} dager"
                    }
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    // Rain axis increases in steps by 3 days
                    count = IndicatorCount.StepBased(stepBy = 3.0),
                    contentBuilder = { value ->
                        "%.0f".format(value)
                    }
                ),
                gridProperties = GridProperties(
                    enabled = true,
                    xAxisProperties = AxisProperties(
                        color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                        style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                    ),
                    // lineCount 12 for 12 months
                    yAxisProperties = AxisProperties(
                        lineCount = 12,
                        color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                        style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                    )
                ),
                minValue = 0.0,
                maxValue = 21.0, //perhaps 20? explore
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
                            color = SolidColor(TrafficGreen),
                            firstGradientFillColor = TrafficGreen.copy(alpha = .2f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = true,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(BrightWhite),
                                radius = 4.dp,
                                strokeWidth = 2.dp,
                                strokeColor = SolidColor(TrafficGreen)
                            )
                        )
                    )
                },
                labelHelperProperties = LabelHelperProperties(enabled = false),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months
                ),
                popupProperties = PopupProperties(
                    enabled = true,
                    mode = PopupProperties.Mode.PointMode(), // "snaps" to next month
                    animationSpec = tween(300),
                    duration = 2000L,
                    textStyle = TextStyle(
                        color = BrightWhite,
                        fontSize = 11.sp
                    ),
                    containerColor = Charcoal,
                    cornerRadius = 8.dp,
                    contentHorizontalPadding = 8.dp,
                    contentVerticalPadding = 4.dp,
                    contentBuilder = { popup ->
                        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                            "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                        "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)}mm"
                    }
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    enabled = true,
                    // Rain axis increases in steps by 10 cm
                    count = IndicatorCount.StepBased(stepBy = 10.0),
                    contentBuilder = { value ->
                        "%.0f".format(value)
                    }
                ),
                gridProperties = GridProperties(
                    enabled = true,
                    xAxisProperties = AxisProperties(
                        color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                        style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                    ),
                    // lineCount 12 for 12 months
                    yAxisProperties = AxisProperties(
                        lineCount = 12,
                        color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                        style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                    )
                ),
                minValue = 0.0,
                maxValue = 60.0,
            )
        }
    }
}

@Composable
fun SnowChart(
    meanSnowDepth: List<Double>,
    maxSnowDepth: List<Double>,
    modifier: Modifier = Modifier
) {
    val months = listOf("Jan", " ", " ", "Apr", " ", "Jun", " ", " ", "Sep", " ", " ", "Des")

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = "Snødybde per måned",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            data = remember(meanSnowDepth, maxSnowDepth) {
                listOf(
                    Line(
                        label = "Snittdybde (cm)",
                        values = meanSnowDepth,
                        color = SolidColor(MayaBlue),
                        firstGradientFillColor = MayaBlue.copy(alpha = .2f),
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(MayaBlue)
                        )
                    ),
                    Line(
                        label = "Maksdybde (cm)",
                        values = maxSnowDepth,
                        color = SolidColor(DarkBlue),
                        firstGradientFillColor = Color.Transparent,
                        secondGradientFillColor = Color.Transparent,
                        curvedEdges = true,
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(BrightWhite),
                            radius = 4.dp,
                            strokeWidth = 2.dp,
                            strokeColor = SolidColor(DarkBlue)
                        )
                    )
                )
            },
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            popupProperties = PopupProperties(
                enabled = true,
                mode = PopupProperties.Mode.PointMode(), // "snaps" to next month
                animationSpec = tween(300),
                duration = 2000L,
                textStyle = TextStyle(
                    color = BrightWhite,
                    fontSize = 11.sp
                ),
                containerColor = Charcoal,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 8.dp,
                contentVerticalPadding = 4.dp,
                contentBuilder = { popup ->
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                    "${monthNames[popup.valueIndex]}: ${"%.1f".format(popup.value)} cm"
                }
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                // Depth axis increases in steps by 20 cm
                count = IndicatorCount.StepBased(stepBy = 20.0),
                contentBuilder = { value ->
                    "%.0f".format(value)
                }
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = AxisProperties(
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                ),
                // lineCount 12 for 12 months
                yAxisProperties = AxisProperties(
                    lineCount = 12,
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                )
            ),
            minValue = 0.0,
            maxValue = 120.0,
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

