package no.uio.ifi.in2000.team20.team20app.ui.screens.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel,
    savedViewModel: SavedViewModel,
    onSavedClick: (Location) -> Unit
) {
    val saved by savedViewModel.saved.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (saved.isEmpty()) {
            Text(
                text = "Du har ingen lagrede steder enda.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(saved) { area ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                sharedViewModel.setSelectedArea(area)
                                onSavedClick(area)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = area.name,
                                style = MaterialTheme.typography.titleLarge
                            )

                            val subtitle = listOfNotNull(area.municipality, area.county)
                                .joinToString(", ")

                            if (subtitle.isNotEmpty()) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SavedScreenPreview() {
    MaterialTheme {
    }
}