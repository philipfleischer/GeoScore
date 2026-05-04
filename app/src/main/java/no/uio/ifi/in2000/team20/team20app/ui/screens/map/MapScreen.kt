package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.window.core.layout.WindowSizeClass
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.wms.WmsTileOverlay
import no.uio.ifi.in2000.team20.team20app.R
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass
import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel = viewModel(),
    savedViewModel: SavedViewModel,
    mapViewModel: MapViewModel = viewModel(),
    onOpenSearch: () -> Unit = {},
    onOpenReport: () -> Unit = {}
) {
    //TODO: Import custom colors
    val chosenPosition by sharedViewModel.selectedLocation.collectAsStateWithLifecycle()
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val layers by mapViewModel.layers.collectAsStateWithLifecycle()

    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val cameraPosition = LatLng(chosenPosition.lat, chosenPosition.lon)
    val markerPosition = rememberUpdatedMarkerState(position = cameraPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 10f)
    }

    LaunchedEffect(chosenPosition) {
        savedViewModel.checkIfSaved(chosenPosition)
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(chosenPosition.lat, chosenPosition.lon), 12f
            )
        )
    }

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
            ),
            onMapLongClick = {latlng ->
                val lat = BigDecimal(latlng.latitude).setScale(5, RoundingMode.HALF_UP).toDouble()
                val lon = BigDecimal(latlng.longitude).setScale(5, RoundingMode.HALF_UP).toDouble()
                sharedViewModel.setSelectedArea(
                    Location(
                        address = "$lat, $lon",
                        municipality = null,
                        county = null,
                        lat = lat,
                        lon = lon
                    )
                )
            }
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
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(
                    top = if (compactScreenWidth) 48.dp else 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = onOpenSearch,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Søk etter adresse",
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (chosenPosition != null) {
                    Button(onClick = onOpenReport) {
                        Text("Vis rapport")
                    }
                }
            }

            Text(
                text = chosenPosition.name,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 30.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() }
            )
        }
    }
}


