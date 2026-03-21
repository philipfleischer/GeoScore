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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import no.uio.ifi.in2000.team20.team20app.data.wmsToWmts.TileURLFromWmsProvider
import no.uio.ifi.in2000.team20.team20app.navigation.Route
import no.uio.ifi.in2000.team20.team20app.ui.components.ScreenScaffold
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_LATITUDE
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_LONGITUDE
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.TILE_SIZE

//TODO: Not hardcoded obviously
val currTileProvider = TileURLFromWmsProvider("", TILE_SIZE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    selectedAreaName: String,
    onAreaSelected: (String, Double, Double) -> Unit,
    onOpenDetails: (String, Double, Double) -> Unit
) {
    //TODO: PUT THIS IN A VIEWMODEL OR SOMETHING IDK, APPSTATE?? GOOGLE EXAMPLE IS IN A COMPOSABLE SO IDK
    val default = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    val defaultMarkerState = rememberUpdatedMarkerState(position = default)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(default, 10f)
    }


    BottomSheetScaffold(

        sheetPeekHeight = 150.dp, // ca 1 cm synlig når kollapset, TODO: Øk synligheten?

        sheetContainerColor = MaterialTheme.colorScheme.surface,

        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Drag handle - Gir brukeren visuelt hint om at sheet kan dras oppover.

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
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colorScheme.surfaceVariant),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("Kart-placeholder")
//            }
            //TODO: Separate GoogleMap to its own file
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    maxZoomPreference = MAX_ZOOM,
                    minZoomPreference = MIN_ZOOM
                )
            ){
                TileOverlay(
                    tileProvider = currTileProvider,
                    visible = true
                )
                Marker(
                    state = defaultMarkerState,
                    title = "Bergen",
                    snippet = "Markør for Bergen"
                )
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
        ScreenScaffold(
            goToHome = {},
            goToMap = {},
            goToFavorites = {},
            currentDestination = Route.MapDestination
        ) {
            MapScreen(
                selectedAreaName = "Bergen",
                onAreaSelected = { _, _, _ -> },
                onOpenDetails = { _, _, _ -> }
            )
        }
    }
}