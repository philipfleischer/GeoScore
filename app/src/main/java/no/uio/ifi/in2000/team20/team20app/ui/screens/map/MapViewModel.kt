package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class MapViewModel(): ViewModel() {
    private val _layers: MutableStateFlow<List<MapLayer>> = MutableStateFlow(MapLayers.layers)
    val layers: StateFlow<List<MapLayer>> = _layers.asStateFlow()

    private val _layersExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val layersExpanded: StateFlow<Boolean> = _layersExpanded.asStateFlow()

    fun toggleLayersExpanded() {
        _layersExpanded.value = !_layersExpanded.value
    }

    fun toggleLayer(layerId: Int){
        _layers.update { layers ->
            layers.map { layer ->
                if (layer.layerId == layerId) {
                    layer.copy(toggled = !layer.toggled)
                } else {
                    layer.copy(toggled = false)
                }
            }
        }
    }

}