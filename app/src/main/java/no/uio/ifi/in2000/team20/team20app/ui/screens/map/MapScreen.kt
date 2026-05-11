package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
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
    val layersExpanded by mapViewModel.layersExpanded.collectAsStateWithLifecycle()

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
                    visible = layer.layerId == mapViewModel.selectedLayer.value.layerId
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = onOpenSearch,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clip(CircleShape).size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
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

            if (chosenPosition != null) {
                Text(
                    text = chosenPosition!!.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Row{
                Spacer(Modifier.weight(1f))
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clip(CircleShape).size(48.dp),
                    onClick = mapViewModel::toggleLayersExpanded
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                if (layersExpanded) {
                    Column(
                        modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                modifier = Modifier.padding(start = 15.dp),
                                text = "Kartlag"
                            )
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                onClick = mapViewModel::toggleLayersExpanded
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        LazyRow() {
                            item {
                                layers.forEach { layer ->
                                    MapLayerSelectable(
                                        modifier = Modifier.size(100.dp),
                                        layer = layer,
                                        onClick = { mapViewModel.setActiveLayer(layer) }
                                    )
                                }
                            }
                        }
                        DisplayLegend(
                            modifier = Modifier.size(20.dp),
                            layer = mapViewModel.selectedLayer.collectAsStateWithLifecycle().value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapLayerSelectable(
    modifier: Modifier = Modifier,
    layer: MapLayer,
    onClick: () -> Unit
){
    Column(
        modifier = modifier,

    ) {
        Button(
            onClick = onClick
        ) {
            AsyncImage(
                model = layer.imageURI,
                contentDescription = layer.name
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(1f),
            text = layer.name,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun DisplayLegend(
    modifier: Modifier = Modifier,
    layer: MapLayer
) {
    Row {
        if(layer.legendURI.isEmpty()){
            Row(Modifier.height(24.dp)){}
        }
        layer.legendURI.forEach {
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(start = 15.dp).height(24.dp)
                ) {
                    Box( //We need contrast for the legend
                        modifier = Modifier.fillMaxHeight(1f).aspectRatio(1f).background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            modifier = modifier,
                            model = it.second,
                            contentDescription = it.first
                        )
                    }
                    Text(
                        text = it.first,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

