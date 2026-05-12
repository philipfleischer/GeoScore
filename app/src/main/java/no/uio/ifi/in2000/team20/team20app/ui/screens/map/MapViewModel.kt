package no.uio.ifi.in2000.team20.team20app.ui.screens.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team20.team20app.data.repository.MapLayerRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.mapLayer.MapLayer
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
class MapViewModel @Inject constructor(
    private val mapLayerRepository: MapLayerRepository
) : ViewModel() {
    private val _layers = MutableStateFlow(mapLayerRepository.getMapLayers())
    val layers = _layers.asStateFlow()

    private val _selectedLayer: MutableStateFlow<MapLayer> = MutableStateFlow(_layers.value[0])
    val selectedLayer: StateFlow<MapLayer> = _selectedLayer.asStateFlow()
    private val _layersExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val layersExpanded: StateFlow<Boolean> = _layersExpanded.asStateFlow()

    fun toggleLayersExpanded() {
        _layersExpanded.value = !_layersExpanded.value
    }

    fun setActiveLayer(layer: MapLayer) {
        _selectedLayer.value = layer
    }
}