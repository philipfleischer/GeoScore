package no.uio.ifi.in2000.team20.team20app.ui.screens.result

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import ir.ehsannarmani.compose_charts.models.ZeroLineProperties
import no.uio.ifi.in2000.team20.team20app.ui.theme.Charcoal
import no.uio.ifi.in2000.team20.team20app.ui.theme.SlateGray
import no.uio.ifi.in2000.team20.team20app.ui.theme.White
import kotlin.math.abs

@Composable
fun GenericLineChart(
    data: List<Line>,
    title: String,
    chartHeight: Dp,
    minValue: Double,
    maxValue: Double,
    unitSuffix: String, // Text in the popup, Jan: 0.5 "timer"
    indicatorCount: IndicatorCount,
    modifier: Modifier = Modifier,
    showZeroLine: Boolean = false,
    zeroLineColor: Color = Color.Transparent,
    // Controls the number of vertical grid lines drawn across the chart (one per month by default)
    // Confusing name, yAxisProperties.lineCount draws vertical lines
    verticalGridLineCount: Int = 12
) {
    // Only odd months are shown as labels to avoid crowding on narrow screens.
    // Even-month slots use a single space so the library still spaces all 12 positions evenly.
    val months = listOf("Jan", " ", "Mar", " ", "Mai", " ",
        "Jul", " ", "Sep", " ", "Nov", " ")

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            data = data,
            zeroLineProperties = if (showZeroLine) {
                ZeroLineProperties(
                    enabled = true,
                    color = SolidColor(zeroLineColor),
                )
            } else {
                ZeroLineProperties(enabled = false)
            },
            labelHelperProperties = LabelHelperProperties(enabled = true),
            labelProperties = LabelProperties(
                enabled = true,
                labels = months
            ),
            popupProperties = PopupProperties(
                enabled = true,
                mode = PopupProperties.Mode.PointMode(threshold = 8.dp),
                animationSpec = tween(300),
                duration = 3000L,
                textStyle = TextStyle(
                    color = White,
                    fontSize = 11.sp
                ),
                containerColor = Charcoal,
                cornerRadius = 8.dp,
                contentHorizontalPadding = 8.dp,
                contentVerticalPadding = 4.dp,
                contentBuilder = { popup ->
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
                        "Jul", "Aug", "Sep", "Okt", "Nov", "Des")
                    // The library's valueIndex can be unreliable in portrait, so we
                    // find the correct month by matching the closest value in the data instead.
                    val values = data.getOrNull(popup.dataIndex)?.values
                    val monthIndex = values
                        ?.mapIndexed { i, v -> i to abs(v - popup.value) }
                        ?.minByOrNull { it.second }
                        ?.first
                        ?: popup.valueIndex
                    "${monthNames.getOrElse(monthIndex) { monthNames.last() }}: ${"%.1f".format(popup.value)}$unitSuffix"
                }
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                count = indicatorCount,
                contentBuilder = { value -> "%.0f".format(value) }
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = AxisProperties(
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                ),
                yAxisProperties = AxisProperties(
                    lineCount = verticalGridLineCount,
                    color = SolidColor(SlateGray.copy(alpha = 0.2f)),
                    style = StrokeStyle.Dashed(intervals = floatArrayOf(6f, 6f))
                )
            ),
            minValue = minValue,
            maxValue = maxValue,
        )
    }
}