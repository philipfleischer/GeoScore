package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    areaName: String,
    onOpenMap: () -> Unit,
    onOpenDetails: () -> Unit,
    onOpenClimateStats: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Naturhendelser",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        //TODO: Endre til nedtrekksmeny?
        item {
            Text(
                text = "Valgt område: $areaName",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Risikokort",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise sammendrag av risiko for området, f.eks: flom, skred og styrtregn.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Koble til ViewModel og hent API-data for det valgte området.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Anbefalte tiltak",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise klimatilpasningstiltak, som drenering.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Sett inn anbefalinger basert på område, hendelsestype og faglige kilder.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Barometer merkning",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vise A-G for rangerings idéen.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "// TODO: Sett inn barometer energimerking ikonet eller hex diagrammet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Button(
                onClick = onOpenDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Se områdedetaljer")
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            areaName = "Oslo",
            onOpenMap = {},
            onOpenDetails = {},
            onOpenClimateStats = {}
        )
    }
}