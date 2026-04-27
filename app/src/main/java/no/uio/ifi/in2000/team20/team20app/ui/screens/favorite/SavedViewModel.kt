package no.uio.ifi.in2000.team20.team20app.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class SavedViewModel(
    private val repository: SavedRepository
) : ViewModel() {

    val saved: StateFlow<List<Location>> =
        repository.getAllSaved()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isCurrentSaved = MutableStateFlow(false)
    val isCurrentSaved: StateFlow<Boolean> = _isCurrentSaved.asStateFlow()

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

class SavedViewModelFactory(
    private val repository: SavedRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SavedViewModel(repository) as T
    }
}