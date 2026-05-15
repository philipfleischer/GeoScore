package no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import javax.inject.Inject


data class LocationWithGeoscore(
    val location: Location,
    val geoscore: Double?
)

fun scoreToGradeVM(score: Double?): String =
    if (score == null) { "?"} else {
        when {
            score < 17 -> "A"
            score < 33 -> "B"
            score < 50 -> "C"
            score < 67 -> "D"
            score < 83 -> "E"
            score >= 83 -> "F"
            else -> "?"
        }
    }

/**
 * ViewModel for managing saved locations.
 * @property repository Repository for accessing saved locations
 * @property saved Flow of all saved locations
 * @property isCurrentSaved Whether the current location is saved
 */

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    val saved: StateFlow<List<LocationWithGeoscore>> =
        repository.getAllSaved()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isCurrentSaved = MutableStateFlow(false)
    val isCurrentSaved: StateFlow<Boolean> = _isCurrentSaved.asStateFlow()

    fun checkIfSaved(location: Location) {
        viewModelScope.launch {
            _isCurrentSaved.value = repository.isSaved(location)
        }
    }

    fun addSaved(location: Location, geoScore: Double?) {
        viewModelScope.launch {
            repository.addSaved(LocationWithGeoscore(
                location = location,
                geoscore = geoScore
            ))
            _isCurrentSaved.value = true
        }
    }

    fun removeSaved(location: Location) {
        viewModelScope.launch {
            repository.removeSaved(location)
            _isCurrentSaved.value = false
        }
    }
}