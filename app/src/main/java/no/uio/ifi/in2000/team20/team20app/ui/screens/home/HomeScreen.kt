package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.ui.components.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    sharedViewModel: AppViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    theme: MaterialTheme = MaterialTheme,
) {
    val location by sharedViewModel.selectedLocation.collectAsStateWithLifecycle()

    // Calculates window width and returns true if the size width class is compact, and false for everything else.
    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(MEDIUM_SCREEN_WIDTH)
    val paddingValue = DEFAULT_PADDING_DP

    LaunchedEffect(location) {
        if(location != null) {
            savedViewModel.checkIfSaved(location!!)
        }
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
        ,
        columns = GridCells.Fixed(if(compactScreenWidth) 1 else 2),
        contentPadding = PaddingValues(
            start = (paddingValue*2).dp,
            end = if (compactScreenWidth) (paddingValue*2).dp else (paddingValue*5).dp,
            top = (paddingValue*5).dp,
            bottom = (paddingValue*2).dp,
        ),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        item {
            //Column for header and infotext
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth(if (compactScreenWidth) 1f else 0.5f)
                    .semantics {
                        isTraversalGroup = true
                    }
            ){
                Text(
                    text = "Vit hva du kjøper - før du kjøper det",
                    fontSize = 40.sp,
                    lineHeight = 48.sp,
                    color = theme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics{ heading() }
                )
                Spacer(Modifier.height(paddingValue.dp))
                Text(
                    text = "Få innsikt i grunnforhold og naturfare. Søk opp en adresse og få en risikovurdering " +
                            "basert på geologisk og meterologisk data.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = theme.colorScheme.onSurface
                )
            }
        }
        item {
            SearchBarObject(onOpenSearch = onOpenSearch, text  = "Søk etter en adresse...")
        }
    }
}

