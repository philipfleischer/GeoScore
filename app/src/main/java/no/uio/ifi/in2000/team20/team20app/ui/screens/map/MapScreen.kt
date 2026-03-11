package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    selectedAreaName: String,
    onAreaSelected: (String, Double, Double) -> Unit,
    onOpenDetails: (String, Double, Double) -> Unit
) {

    BottomSheetScaffold(

        sheetPeekHeight = 64.dp, // ca 1 cm synlig når kollapset, TODO: Øk synligheten

        sheetContainerColor = MaterialTheme.colorScheme.surface,

        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                //Drag handle - Gir brukeren visuelt hint om at sheet kan dras oppover.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .heightIn(min = 4.dp)
                        .fillMaxWidth(0.15f)
                        .background(
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(50)
                        )
                )

                Text(
                    text = selectedAreaName,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Sammendrag",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Kort risikoanalyse for området: flom, skred eller styrtregn.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mitigerende tiltak",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Eksempel: forbedret drenering eller terrengtilpasning.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Button(
                    onClick = { onOpenDetails(selectedAreaName, 59.9139, 10.7522) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Åpne full detaljvisning")
                }
            }
        }

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // kartet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Kart-placeholder")
            }

            // Informasjonskort
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Valgt område")
                    Text(selectedAreaName)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MapScreenPreview() {
    MaterialTheme {
        MapScreen(
            selectedAreaName = "Bergen",
            onAreaSelected = { _, _, _ -> },
            onOpenDetails = { _, _, _ -> }
        )
    }
}