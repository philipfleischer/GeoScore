package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ClimateStatsScreen(
    areaName: String,
    latitude: Double,
    longitude: Double,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Klimastatistikk for $areaName",
            style = MaterialTheme.typography.headlineLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nedbør", style = MaterialTheme.typography.titleLarge)
                Text("// TODO: Hent historiske/aggregerte nedbørsdata for $areaName ($latitude, $longitude)")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Vind", style = MaterialTheme.typography.titleLarge)
                Text("// TODO: Hent vinddata og vis relevante verdier eller trender her")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Flomrelevant risiko", style = MaterialTheme.typography.titleLarge)
                Text("// TODO: Hent og vis relevante risikoer for naturfare her")
            }
        }

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tilbake")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClimateStatsScreenPreview() {
    MaterialTheme {
        ClimateStatsScreen(
            areaName = "Trondheim",
            latitude = 63.4305,
            longitude = 10.3951,
            onBackClick = {}
        )
    }
}