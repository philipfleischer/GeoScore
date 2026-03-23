package no.uio.ifi.in2000.team20.team20app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import no.uio.ifi.in2000.team20.team20app.ui.theme.CustomTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.darkThemeColors
import no.uio.ifi.in2000.team20.team20app.ui.theme.lightThemeColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            val theme : CustomTheme = if(isSystemInDarkTheme()) darkThemeColors else lightThemeColors
            CompositionLocalProvider (LocalTheme provides theme) {
                MaterialTheme {
                    NaturhendelserApp()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}