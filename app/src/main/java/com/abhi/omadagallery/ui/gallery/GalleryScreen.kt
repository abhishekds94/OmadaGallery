package com.abhi.omadagallery.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abhi.omadagallery.R
import com.abhi.omadagallery.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun GalleryScreen(
    vm: GalleryViewModel = hiltViewModel(),
    onOpen: (Photo) -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.handleIntent(GalleryIntent.LoadInitial)
    }

    GalleryScreenContent(
        state = state,
        onSearch = { it ->
            vm.handleIntent(GalleryIntent.Search(it))
        },
        onOpen = onOpen,
        onRetry = {
            vm.handleIntent(GalleryIntent.Retry)
        },
        onLoadNext = {
            vm.handleIntent(GalleryIntent.LoadNextPage)
        },
        effects = vm.effects
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryScreenContent(
    state: GalleryState,
    onSearch: (String) -> Unit,
    onOpen: (Photo) -> Unit,
    onRetry: () -> Unit,
    onLoadNext: () -> Unit,
    effects: Flow<GalleryEffect>? = null
) {
    val snackHost = remember { SnackbarHostState() }

    LaunchedEffect(effects) {
        effects?.collect {
            if (it is GalleryEffect.ShowMessage) {
                snackHost.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = state.query.orEmpty(),
                onSubmit = onSearch
            )
        },
        snackbarHost = { SnackbarHost(snackHost) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            val gridState = rememberLazyGridState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    state.items,
                    key = { index, item -> "${item.id}-$index" }) { _, photo ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photo.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = photo.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .combinedClickable(
                                onClick = { onOpen(photo) }
                            )
                    )
                }

                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    when {
                        state.isLoading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                        !state.endReached && state.items.isNotEmpty() -> {
                            LaunchedEffect(state.items.size, state.page) {
                                onLoadNext()
                            }
                        }
                    }
                }
            }

            if (state.error != null && state.items.isEmpty()) {
                FilledTonalButton(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) { Text(stringResource(R.string.btn_retry)) }
                println("[omada123] error is: ${state.error}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(query: String, onSubmit: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf(query) }
    TopAppBar(
        title = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search_photos)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit(text) }),
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

/* -------------------- PREVIEWS -------------------- */

private fun samplePhotos() = listOf(
    Photo(
        id = "1",
        title = "City Lights",
        thumbnailUrl = "https://picsum.photos/200?1",
        fullUrl = "https://picsum.photos/800?1"
    ),
    Photo(
        id = "2",
        title = "Sunset Bay",
        thumbnailUrl = "https://picsum.photos/200?2",
        fullUrl = "https://picsum.photos/800?2"
    ),
    Photo(
        id = "3",
        title = "Forest Trail",
        thumbnailUrl = "https://picsum.photos/200?3",
        fullUrl = "https://picsum.photos/800?3"
    ),
)

@Preview(showBackground = true, name = "Gallery – Light")
@Preview(
    showBackground = true,
    name = "Gallery – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun GalleryScreen_Preview() {
    val previewState = remember {
        GalleryState(
            query = "city",
            items = samplePhotos() + samplePhotos(),
            page = 1,
            totalPages = 5,
            isLoading = false,
            endReached = false,
            error = null
        )
    }
    MaterialTheme {
        GalleryScreenContent(
            state = previewState,
            onSearch = {},
            onOpen = {},
            onRetry = {},
            onLoadNext = {},
            effects = emptyFlow()
        )
    }
}

@Preview(showBackground = true, name = "Gallery – Empty")
@Composable
private fun GalleryScreen_Empty_Preview() {
    MaterialTheme {
        GalleryScreenContent(
            state = GalleryState(),
            onSearch = {},
            onOpen = {},
            onRetry = {},
            onLoadNext = {},
            effects = emptyFlow()
        )
    }
}
