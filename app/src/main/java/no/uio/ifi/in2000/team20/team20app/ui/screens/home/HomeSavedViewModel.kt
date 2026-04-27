package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.domain.model.Location

class HomeSavedViewModel(
    private val repository: SavedRepository
) : ViewModel() {

    fun addSaved(location: Location) {
        viewModelScope.launch {
            repository.addSaved(location)
        }
    }
}

class HomeSavedViewModelFactory(
    private val repository: SavedRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeSavedViewModel(repository) as T
    }
}