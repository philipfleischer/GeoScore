package no.uio.ifi.in2000.team20.team20app.ui.screens.details


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.ui.components.SharedTopAppBar

@Composable
fun AreaDetailsScreen(
    areaName: String,
    latitude: Double,
    longitude: Double,
    onBackClick: () -> Unit,
    onOpenClimateStats: () -> Unit
) {
    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = areaName,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Koordinater: $latitude, $longitude",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Farevurdering", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tekstlig fare beskrivelse.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("// TODO: Hent og vis detaljdata fra API for området.")
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mitigerende tiltak", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Eksempel: forbedret drenering.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("// TODO: Sett inn faglig begrunnede anbefalinger basert på datakilder og risikotype.")
                    }
                }
            }

            item {
                Button(
                    onClick = onOpenClimateStats,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se klimastatistikk")
                }
            }
        }
    }
}

// preview for Voss koordinater, template ikke riktig data
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AreaDetailsScreenPreview() {
    MaterialTheme {
        AreaDetailsScreen(
            areaName = "Voss",
            latitude = 60.6287,
            longitude = 6.4147,
            onBackClick = {},
            onOpenClimateStats = {}
        )
    }
}