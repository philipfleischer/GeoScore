package no.uio.ifi.in2000.team20.team20app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import no.uio.ifi.in2000.team20.team20app.navigation.AppState
import no.uio.ifi.in2000.team20.team20app.navigation.NavigationRoot
import no.uio.ifi.in2000.team20.team20app.navigation.Route


@Composable
fun NaturhendelserApp() {
    val appState = remember { AppState() }
    NavigationRoot(appState = appState)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NaturhendelserAppPreview() {
    MaterialTheme {
        NaturhendelserApp()
    }
}