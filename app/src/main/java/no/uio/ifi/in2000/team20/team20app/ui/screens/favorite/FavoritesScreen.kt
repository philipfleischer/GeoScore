package no.uio.ifi.in2000.team20.team20app.ui.screens.favorite

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel = viewModel()
) {
    val favorites = listOf(
        Location(address="Oslo", municipality = null, county = null, lat = 59.9139, lon = 10.7522)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "// TODO: Koble skjermen til lagrede favorittområder fra lokal database.",
            style = MaterialTheme.typography.bodyMedium
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(favorites) { area ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sharedViewModel.setSelectedArea(area)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = area.name, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Koordinater: ${area.lat}, ${area.lon}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesScreen()
    }
}