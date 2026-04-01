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
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    val favorites: StateFlow<List<Location>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isCurrentFavorite = MutableStateFlow(false)
    val isCurrentFavorite: StateFlow<Boolean> = _isCurrentFavorite.asStateFlow()

    fun checkIfFavorite(location: Location) {
        viewModelScope.launch {
            _isCurrentFavorite.value = repository.isFavorite(location)
        }
    }

    fun addFavorite(location: Location) {
        viewModelScope.launch {
            repository.addFavorite(location)
            _isCurrentFavorite.value = true
        }
    }

    fun removeFavorite(location: Location) {
        viewModelScope.launch {
            repository.removeFavorite(location)
            _isCurrentFavorite.value = false
        }
    }
}

class FavoritesViewModelFactory(
    private val repository: FavoritesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}