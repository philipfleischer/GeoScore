package no.uio.ifi.in2000.team20.team20app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavigationRoot(){
    //Initialize viewmodels, httpclients etc here?
    val backStack = rememberNavBackStack(Route.HomeScreen)
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

        }
    )
}