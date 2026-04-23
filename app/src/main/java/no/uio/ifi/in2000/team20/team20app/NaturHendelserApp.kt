package no.uio.ifi.in2000.team20.team20app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import no.uio.ifi.in2000.team20.team20app.data.api.FrostClientProvider
import no.uio.ifi.in2000.team20.team20app.data.api.GeoSearchClientProvider
import no.uio.ifi.in2000.team20.team20app.data.datasource.AddressRemoteDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.FrostDataSource
import no.uio.ifi.in2000.team20.team20app.data.datasource.LocationRemoteDatasource
import no.uio.ifi.in2000.team20.team20app.data.local.AppDatabase
import no.uio.ifi.in2000.team20.team20app.data.repository.FavoritesRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.domain.usecase.GetHazardScore
import no.uio.ifi.in2000.team20.team20app.ui.navigation.NavigationRoot
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModelFactory
import no.uio.ifi.in2000.team20.team20app.util.Constants

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
    val geoSearchRepository = remember {
        GeoSearchRepository(
            locationDatasource = LocationRemoteDatasource(
                client = GeoSearchClientProvider.client
            ),
            addressDatasource = AddressRemoteDataSource(
                client = GeoSearchClientProvider.client
            )
        )
    }

    val frostRepository = remember {
        FrostRepository(
            dataSource = FrostDataSource(
                client = FrostClientProvider.client,
                credentials = "${Constants.FROST_CLIENT_ID}:${Constants.FROST_CLIENT_SECRET}"
            )
        )
    }

    val searchViewModel: SearchViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SearchViewModel(repository = geoSearchRepository)
            }
        }
    )

    val homeViewModel: HomeViewModel = viewModel()

    val frostViewModel: FrostViewModel = viewModel(
        factory = FrostViewModelFactory(frostRepository)
    )

    val appViewModel: AppViewModel = viewModel()

    NavigationRoot(
        appViewModel = appViewModel,
        searchViewModel = searchViewModel,
        homeViewModel = homeViewModel,
        frostViewModel = frostViewModel,
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