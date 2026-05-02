package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.maps.android.compose.wms.WmsTileOverlay
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.ClimateAdjustedFlood1000YearUrlFormatter
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.rockUrlFormatter
import no.uio.ifi.in2000.team20.team20app.R
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.ClimateAdjustedFlood200YearUrlFormatter
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.ClimateAdjustedFlood20YearUrlFormatter
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.QuickClayUrlFormatter
import no.uio.ifi.in2000.team20.team20app.util.WmsFormatterLambdas.RadonUrlFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel = viewModel(),
    savedViewModel: SavedViewModel,
    mapViewModel: MapViewModel = viewModel(),
    onOpenSearch: () -> Unit = {}
) {
    //TODO: Import custom colors
    val chosenPosition = sharedViewModel.selectedLocation
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val layers by mapViewModel.layers.collectAsStateWithLifecycle()

    val cameraPosition = LatLng(chosenPosition.lat, chosenPosition.lon)
    val markerPosition = rememberUpdatedMarkerState(position = cameraPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 10f)
    }

    LaunchedEffect(chosenPosition) {
        savedViewModel.checkIfSaved(chosenPosition)
    }

//    BottomSheetScaffold(
//        modifier = modifier,
//        sheetPeekHeight = 100.dp,
//        sheetContainerColor = MaterialTheme.colorScheme.surface,
//        sheetContent = {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Text(
//                    text = chosenPosition.name,
//                    style = MaterialTheme.typography.headlineMedium
//                )
//
//                Text(
//                    text = "Her vil du kunne se historisk klimadata en gang i fremtiden!",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//
//                // TODO: Add historical climate charts here (temperature, snow, precipitation, wind)
//                // FrostViewModel must be passed in and loadFrostStats() called on location change
//                // See ClimateStatsScreen.kt for chart implementations
//            }
//        }
//    ) { paddingValues ->
//
//
//    }
    Box(
        modifier = Modifier
            .fillMaxSize()
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
            layers.forEach { layer ->
                WmsTileOverlay(
                    urlFormatter = layer.formatter,
                    visible = layer.toggled
                )
            }
            Marker(
                state = markerPosition,
                title = chosenPosition.name,
                snippet = "Markør for ${chosenPosition.name}"
            )
        }
        Text(
            text = chosenPosition.name,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 30.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics{ heading() }.fillMaxWidth().padding(32.dp)
        )

        FloatingActionButton(
            onClick = onOpenSearch,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Søk etter adresse"
            )
        }

//        Card(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
//                .fillMaxWidth(),
//            shape = RoundedCornerShape(20.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer
//            )
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text("Valgt område")
//                Text(chosenPosition.name)
//            }
//        }
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
