package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for MapScreen.
 *
 * Responsibility:
 * - Manage map layers
 * - Fetch hazard data
 *
 * Why:
 * Keeps map logic outside UI.
 */

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _layers: MutableStateFlow<List<MapLayer>> = MutableStateFlow(MapLayers.layers)
    val layers: StateFlow<List<MapLayer>> = _layers.asStateFlow()

    private val _selectedLayer: MutableStateFlow<MapLayer> = MutableStateFlow(MapLayers.layers[0])
    val selectedLayer: StateFlow<MapLayer> = _selectedLayer.asStateFlow()
    private val _layersExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val layersExpanded: StateFlow<Boolean> = _layersExpanded.asStateFlow()

    fun toggleLayersExpanded() {
        _layersExpanded.value = !_layersExpanded.value
    }

    fun setActiveLayer(layer: MapLayer) {
        _selectedLayer.value = layer
    }
//    fun toggleLayer(layerId: Int){
//        _layers.update { layers ->
//            layers.map { layer ->
//                if (layer.layerId == layerId) {
//                    layer.copy(toggled = !layer.toggled)
//                } else {
//                    layer.copy(toggled = false)
//                }
//            }
//        }
//    }

}