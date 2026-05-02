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
import no.uio.ifi.in2000.team20.team20app.data.local.AppDatabase
import no.uio.ifi.in2000.team20.team20app.data.repository.SavedRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.FrostRepository
import no.uio.ifi.in2000.team20.team20app.data.repository.GeoSearchRepository
import no.uio.ifi.in2000.team20.team20app.ui.navigation.NavigationRoot
import no.uio.ifi.in2000.team20.team20app.ui.screens.home.HomeViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.map.MapViewModel
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants

@Composable
fun NaturhendelserApp() {
    val context = LocalContext.current

    val database = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "team20_app_db"
        ).fallbackToDestructiveMigration().build()
    }

    val savedRepository = remember {
        SavedRepository(database.savedLocationDao())
    }
    val geoSearchRepository = remember {
        GeoSearchRepository(
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

    val homeViewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                HomeViewModel()
            }
        }
    )

    val frostViewModel: FrostViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FrostViewModel(frostRepository)
            }
        }
    )

    val mapViewModel: MapViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                MapViewModel()
            }
        }
    )

    val appViewModel: AppViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AppViewModel()
            }
        }
    )

    NavigationRoot(
        appViewModel = appViewModel,
        searchViewModel = searchViewModel,
        homeViewModel = homeViewModel,
        mapViewModel = mapViewModel,
        frostViewModel = frostViewModel,
        savedRepository = savedRepository
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NaturhendelserAppPreview() {
    MaterialTheme {
        NaturhendelserApp()
    }
}