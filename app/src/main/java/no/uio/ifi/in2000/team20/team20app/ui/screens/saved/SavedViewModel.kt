package no.uio.ifi.in2000.team20.team20app.ui.screens.saved

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

/**
 * ViewModel for managing saved locations.
 * @property repository Repository for accessing saved locations
 * @property saved Flow of all saved locations
 */
// TODO: This is accessed across screens. Should be shared.
@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    val saved: StateFlow<List<Location>> =
        repository.getAllSaved()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // TODO: Is isCurrentSaved coupled with selectedLocation in general? Should it be? Should it not? Only used together in MapScreen, but the result is never accessed!
    // TODO: The only usages of saved are passed as parameters to new screens. This will cause recomposition on toggle right? We really only need to recompose the icon.
    private val _isCurrentSaved = MutableStateFlow(false)
    val isCurrentSaved: StateFlow<Boolean> = _isCurrentSaved.asStateFlow()


    //TODO: Suspend and on the Dispatchers.IO right? Every single one of these.
    fun checkIfSaved(location: Location) {
        viewModelScope.launch {
            _isCurrentSaved.value = repository.isSaved(location)
        }
    }

    fun addSaved(location: Location) {
        viewModelScope.launch {
            repository.addSaved(location)
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