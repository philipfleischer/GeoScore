package no.uio.ifi.in2000.team20.team20app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.ui.theme.CustomTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.DarkColorScheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.LightColorScheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.darkThemeColors
import no.uio.ifi.in2000.team20.team20app.ui.theme.lightThemeColors
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()

        window.navigationBarColor = android.graphics.Color.parseColor("#F3F5F6") // Platinum

        super.onCreate(savedInstanceState)
        setContent {
            val theme : CustomTheme = if(isSystemInDarkTheme()) darkThemeColors else lightThemeColors
            val colorScheme = if(isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            CompositionLocalProvider (
                LocalTheme provides theme,
                LocalWindowSizeClass provides windowSizeClass,
            ) {
                MaterialTheme(colorScheme = colorScheme) {
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