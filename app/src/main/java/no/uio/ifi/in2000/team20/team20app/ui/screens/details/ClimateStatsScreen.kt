package no.uio.ifi.in2000.team20.team20app.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.components.SharedTopAppBar

@Composable
fun ClimateStatsScreen(
    location: Location,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "Klimastatistikk",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nedbør", style = MaterialTheme.typography.titleLarge)
                    Text("// TODO: Hent historiske/aggregerte nedbørsdata for $location.name ($location.lat, $location.lon)")
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClimateStatsScreenPreview() {
    MaterialTheme {
        ClimateStatsScreen(
            location = Location(address= "Trondheim",
            lat= 63.4305,
            lon = 10.3951),
            onBackClick = {}
        )
    }
}