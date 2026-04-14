package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.ui.components.ScreenScaffold
import no.uio.ifi.in2000.team20.team20app.ui.components.map.WmsTileOverlayEpsg3857
import no.uio.ifi.in2000.team20.team20app.ui.navigation.Route
import no.uio.ifi.in2000.team20.team20app.ui.screens.favorite.FavoritesViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_ZOOM
import no.uio.ifi.in2000.team20.team20app.R

//TODO: Not hardcoded obviously
val steinUrl = "https://geo.ngu.no/mapserver/NatursteinWMS3?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=TRUE&CRS=EPSG%3A3857&BGCOLOR=0xfffefd&STYLES=default%2C%2C%2C%2C%2C%2C%2C%2C%2C&EXCEPTIONS=inimage&LAYERS=Skifer_og_hellestein_samlet%2CGranitt_og_andre_storkningsbergarter_samlet%2CKleberstein_og_serpentinitt_samlet%2CMarmor_og_kalkstein_samlet%2CGneis_samlet%2CSandstein_og_konglomerat_samlet%2CBrynestein_samlet%2CAnnen_blokkstein_samlet%2CKvernstein_samlet%2CMurestein_samlet&WIDTH=256&HEIGHT=256&BBOX="
val steinUrlFormatterer = {xMin: Double, yMin: Double, xMax: Double, yMax: Double -> "$steinUrl$xMin,$yMin,$xMax,$yMax" }
val fylkeUrlRettFraAI = "https://wms.geonorge.no/skwms1/wms.adm_enheter2?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&LAYERS=fylker&STYLES=default&CRS=EPSG:3857&WIDTH=256&HEIGHT=256&FORMAT=image/png&TRANSPARENT=TRUE&BBOX="
val fylkeUrlFormatterer = {xMin: Double, yMin: Double, xMax: Double, yMax: Double -> "$fylkeUrlRettFraAI$xMin,$yMin,$xMax,$yMax" }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel
) {
    val chosenPosition = sharedViewModel.selectedLocation
    val isCurrentFavorite by favoritesViewModel.isCurrentFavorite.collectAsStateWithLifecycle()

    val cameraPosition = LatLng(chosenPosition.lat, chosenPosition.lon)
    val markerPosition = rememberUpdatedMarkerState(position = cameraPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 10f)
    }

    LaunchedEffect(chosenPosition) {
        favoritesViewModel.checkIfFavorite(chosenPosition)
    }

    BottomSheetScaffold(
        modifier = modifier,
        sheetPeekHeight = 100.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = chosenPosition.name,
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
                    onClick = {
                        if (isCurrentFavorite) {
                            favoritesViewModel.removeFavorite(chosenPosition)
                        } else {
                            favoritesViewModel.addFavorite(chosenPosition)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCurrentFavorite) "Fjern fra favoritter" else "Legg til i favoritter")
                }

                Button(
                    onClick = {},
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
            val context = LocalContext.current
            val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    maxZoomPreference = MAX_ZOOM,
                    minZoomPreference = MIN_ZOOM,
                    mapStyleOptions = mapStyleOptions
                )
            ) {
                WmsTileOverlayEpsg3857(
                    urlFormatter = fylkeUrlFormatterer,
                    visible = true
                )
                Marker(
                    state = markerPosition,
                    title = chosenPosition.name,
                    snippet = "Markør for ${chosenPosition.name}"
                )
            }

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
                    Text(chosenPosition.name)
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun MapScreenPreview() {
//    MaterialTheme {
//        ScreenScaffold(
//            title = "Kart",
//            goToHome = {},
//            goToMap = {},
//            goToFavorites = {},
//            onOpenSettings = {},
//            currentDestination = Route.MapDestination
//        ) {
//            MapScreen(
//                selectedAreaName = "Bergen",
//                onAreaSelected = { _, _, _ -> },
//                onOpenDetails = { _, _, _ -> }
//            )
//        }
//    }
//}
