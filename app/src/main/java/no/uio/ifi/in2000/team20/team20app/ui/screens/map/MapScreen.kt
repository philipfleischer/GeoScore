package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.DefaultMapContentPadding
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.compose.wms.WmsTileOverlay
import no.uio.ifi.in2000.team20.team20app.R
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.SMALL_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.LARGE_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MAX_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH
import no.uio.ifi.in2000.team20.team20app.util.Constants.MIN_ZOOM
import no.uio.ifi.in2000.team20.team20app.util.Constants.ZOOM_ON_LOCATION
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass
import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel,
    mapViewModel: MapViewModel = hiltViewModel(),
    onOpenSearch: () -> Unit = {},
    onOpenReport: () -> Unit = {}
) {

    val padding = DEFAULT_PADDING_DP
    val chosenPosition by sharedViewModel.selectedLocation.collectAsStateWithLifecycle()
    val isCurrentSaved by savedViewModel.isCurrentSaved.collectAsStateWithLifecycle()
    val layers by mapViewModel.layers.collectAsStateWithLifecycle()

    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(MEDIUM_SCREEN_WIDTH)

    val cameraPosition =
        if(chosenPosition != null) {
            LatLng(chosenPosition!!.lat, chosenPosition!!.lon)
        }else{
            sharedViewModel.defaultCameraPosition
        }
    val cameraZoom =
        if(chosenPosition!= null) {
            ZOOM_ON_LOCATION
        }else{
            DEFAULT_ZOOM
        }
    val markerPosition = rememberUpdatedMarkerState(position = cameraPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, cameraZoom)
    }

    LaunchedEffect(chosenPosition) {
        if(chosenPosition != null) {
            savedViewModel.checkIfSaved(chosenPosition!!)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(chosenPosition!!.lat, chosenPosition!!.lon), cameraZoom
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
//                .windowInsetsPadding(WindowInsets.safeDrawing)
            ,
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
            },
            contentPadding = PaddingValues(
                horizontal = if(!compactScreenWidth) (padding*3).dp else 0.dp,
                vertical = if (compactScreenWidth) (padding*9).dp else 0.dp)
        ) {
            layers.forEach { layer ->
                WmsTileOverlay(
                    urlFormatter = layer.formatter,
                    visible = layer.toggled
                )
            }
            if(chosenPosition != null) {
                Marker(
                    state = markerPosition,
                    title = chosenPosition!!.name,
                    snippet = "Markør for ${chosenPosition!!.name}"
                )
            }
        }
        Column(
            modifier = modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(
                    top = (padding * 3).dp,
                    start = if (compactScreenWidth) padding.dp else 0.dp,
                    end = if (compactScreenWidth) padding.dp else (padding * 4).dp,
                    bottom = padding.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(0.6f)
                ){
                    if (chosenPosition == null){
                        FloatingActionButton(
                            onClick = onOpenSearch,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    } else {
                        SearchBarObject(onOpenSearch = onOpenSearch, text = chosenPosition!!.name)
                    }

                }

                if (chosenPosition != null) {
                    Button(
                        onClick = onOpenReport,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Vis rapport", color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        }
    }
}


