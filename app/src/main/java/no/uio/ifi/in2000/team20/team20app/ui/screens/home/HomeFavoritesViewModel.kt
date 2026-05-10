package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class HomeFavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    fun addFavorite(location: Location) {
        viewModelScope.launch {
            repository.addFavorite(location)
        }
    }
}

class HomeFavoritesViewModelFactory(
    private val repository: FavoritesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeFavoritesViewModel(repository) as T
    }
}