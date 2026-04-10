package no.uio.ifi.in2000.team20.team20app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import no.uio.ifi.in2000.team20.team20app.data.api.GeoSearchClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationRemoteDatasource
import no.uio.ifi.in2000.team20.team20app.data.local.AppDatabase
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.ui.navigation.NavigationRoot
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun NaturhendelserApp() {
    val context = LocalContext.current

    val database = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "team20_app_db"
        ).build()
    }

    val favoritesRepository = remember {
        FavoritesRepository(database.favoriteLocationDao())
    }
    val geoSearchRepository = GeoSearchRepository(
        locationDatasource = LocationRemoteDatasource(
            client = GeoSearchClientProvider.client
        ),
        addressDatasource = AddressRemoteDataSource(
            client = GeoSearchClientProvider.client
        )
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SearchViewModel(repository = geoSearchRepository)
            }
        }
    )

    val appViewModel: AppViewModel = viewModel()

    NavigationRoot(
        appViewModel = appViewModel,
        searchViewModel = searchViewModel,
        favoritesRepository = favoritesRepository
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NaturhendelserAppPreview() {
    MaterialTheme {
        NaturhendelserApp()
    }
}