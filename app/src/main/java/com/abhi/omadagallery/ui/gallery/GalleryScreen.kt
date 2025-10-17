package com.abhi.omadagallery.ui.gallery

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.abhi.omadagallery.R
import com.abhi.omadagallery.core.provideImageLoader
import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.ui.NetworkHolderViewModel
import com.abhi.omadagallery.ui.common.RequireOnlineOrSnackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun GalleryScreen(
    vm: GalleryViewModel = hiltViewModel(),
    onOpen: (Photo) -> Unit
) {
    val state by vm.state.collectAsState()
    val snackHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val networkVM: NetworkHolderViewModel = hiltViewModel()
    val isOnline by networkVM.monitor.online.collectAsState()

    LaunchedEffect(Unit) {
        vm.effects.collectLatest { eff ->
            when (eff) {
                is GalleryEffect.ShowMessage -> {
                    snackHost.showSnackbar(
                        message = eff.message,
                        actionLabel = eff.actionLable,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    LaunchedEffect(state.items.isEmpty(), isOnline) {
        if (state.items.isEmpty() && isOnline)
            vm.handleIntent(GalleryIntent.LoadInitial)
    }

    GalleryScreenContent(
        state = state,
        onSearch = { it ->
            RequireOnlineOrSnackbar(isOnline, snackHost, scope) {
                vm.handleIntent(GalleryIntent.Search(it))
            }
        },
        onOpen = { it ->
            RequireOnlineOrSnackbar(isOnline, snackHost, scope) { onOpen(it) }
        },
        onRetry = {
            RequireOnlineOrSnackbar(isOnline, snackHost, scope) {
                vm.handleIntent(GalleryIntent.Retry)
            }
        },
        onLoadNext = {
            RequireOnlineOrSnackbar(isOnline, snackHost, scope) {
                vm.handleIntent(GalleryIntent.LoadNextPage)
            }
        },
        effects = null,
        snackbarHost = snackHost
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
    effects: Flow<GalleryEffect>? = null,
    snackbarHost: SnackbarHostState
) {
    val context = LocalContext.current
    val imageLoader = remember(context) { provideImageLoader(context) }
    val gridState = rememberSaveable(saver = LazyGridState.Saver) { LazyGridState() }

    LaunchedEffect(effects) {
        effects?.collect { effect ->
            if (effect is GalleryEffect.ShowMessage) {
                val result = snackbarHost.showSnackbar(
                    message = effect.message,
                    actionLabel = "Retry",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) onRetry()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(Modifier.fillMaxSize()) {
            StickySearchBar(
                initialQuery = state.query.orEmpty(),
                onSubmit = onSearch
            )
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

                    var imgState by remember {
                        mutableStateOf<AsyncImagePainter.State>(
                            AsyncImagePainter.State.Empty
                        )
                    }

                    AsyncImage(
                        model = ImageRequest
                            .Builder(context)
                            .data(photo.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = photo.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.medium)
                            .combinedClickable(
                                onClick = { onOpen(photo) }
                            )
                    )

                    if (imgState is AsyncImagePainter.State.Loading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    }

                }

                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    when {
                        state.isLoading -> Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

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
    uiMode = Configuration.UI_MODE_NIGHT_YES
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
        val host = remember { SnackbarHostState() }
        GalleryScreenContent(
            state = previewState,
            onSearch = {},
            onOpen = {},
            onRetry = {},
            onLoadNext = {},
            effects = emptyFlow(),
            snackbarHost = host
        )
    }
}

@Preview(showBackground = true, name = "Gallery – Empty")
@Composable
private fun GalleryScreen_Empty_Preview() {
    MaterialTheme {
        val host = remember { SnackbarHostState() }
        GalleryScreenContent(
            state = GalleryState(),
            onSearch = {},
            onOpen = {},
            onRetry = {},
            onLoadNext = {},
            effects = emptyFlow(),
            snackbarHost = host
        )
    }
}
