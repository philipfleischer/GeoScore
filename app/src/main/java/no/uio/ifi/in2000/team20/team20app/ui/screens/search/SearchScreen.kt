package no.uio.ifi.in2000.team20.team20app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.components.ErrorState
import no.uio.ifi.in2000.team20.team20app.ui.components.LoadingState
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    modifier: Modifier = Modifier,
    theme: MaterialTheme = MaterialTheme,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val compactScreenWidth = !LocalWindowSizeClass.current
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val padding = DEFAULT_PADDING_DP

    // Focus the text field when entering the screen and reset search query
    LaunchedEffect(Unit) {
        searchViewModel.resetQuery()
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        top = padding.dp,
                        start = (padding/2).dp,
                        end = if (compactScreenWidth) padding.dp else (padding*3).dp)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Gå tilbake"
                    )
                }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(theme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester),
                    value = uiState.query,
                    onValueChange = { searchViewModel.updateInput(it) },
                    label = { Text("Sted", color = theme.colorScheme.secondary) },
                    placeholder = { Text("Skriv inn adresse...") },
                    isError = uiState.inputError != null,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = theme.colorScheme.surfaceContainerLow,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = theme.colorScheme.secondary,
                        unfocusedTextColor = theme.colorScheme.onSurfaceVariant,
                        cursorColor = theme.colorScheme.secondary,
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = padding.dp,
                    start = padding.dp,
                    end = if (compactScreenWidth) padding.dp else (padding*2).dp,
                )
                .background(theme.colorScheme.surface)
             ,
             verticalArrangement = Arrangement.Top
         ) {
             // Show error message if user enters invalid input
             if (uiState.inputError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.inputError!!,
                    color = theme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.query.isBlank() -> {

                    if(uiState.recentlySearched.isEmpty()){
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = padding.dp)) {
                            Text("Begynn å skrive for å se forslag", color = MaterialTheme.colorScheme.onSurface)
                        }
                    } else {
                        Text(text = "Siste søk")
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                        ) {
                            items(uiState.recentlySearched) { location ->
                                SearchResultItem(
                                    location = location,
                                    onSelect = {
                                        onLocationSelected(location)
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                }
                uiState.isLoading -> LoadingState(modifier = Modifier.fillMaxWidth())
                uiState.error != null -> ErrorState(message = uiState.error!!, modifier = Modifier.fillMaxWidth())
                uiState.results.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text("Ingen resultater for \"${uiState.query}\"", color = theme.colorScheme.error)
                    }
                }
                // Search results
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items(uiState.results) { location ->
                            SearchResultItem(
                                location = location,
                                onSelect = {
                                    searchViewModel.addRecentlySearched(location)
                                    onLocationSelected(location)
                                }
                            )
                            Spacer(modifier = Modifier.height((DEFAULT_PADDING_DP/2).dp))
                        }
                    }
                }
            }
        }
    }


}

@Composable
private fun SearchResultItem(
    location: Location,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ){
        Column(
            modifier = Modifier
                .semantics(mergeDescendants = true){}
                .fillMaxWidth()
                .clickable(onClickLabel = "Select this address") { onSelect() }
                .padding(vertical = 12.dp, horizontal = 4.dp)

            ,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val subtitle = listOfNotNull(location.municipality, location.county).joinToString(", ")
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
