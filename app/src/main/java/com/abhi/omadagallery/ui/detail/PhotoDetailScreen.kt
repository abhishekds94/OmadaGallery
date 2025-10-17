package com.abhi.omadagallery.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abhi.omadagallery.core.provideImageLoader
import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.ui.gallery.GalleryEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotoDetailScreen(
    onBack: () -> Unit,
    vm: PhotoDetailViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    PhotoDetailContent(
        state = state,
        onBack = onBack,
        onRetry = vm::load
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailContent(
    state: DetailState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    effects: Flow<GalleryEffect>? = null
) {
    val snackHost = remember { SnackbarHostState() }
    val context = LocalContext.current
    val imageLoader = remember(context) { provideImageLoader(context) }

    LaunchedEffect(effects) {
        effects?.collect { effect ->
            if (effect is GalleryEffect.ShowMessage) {
                val result = snackHost.showSnackbar(
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
        topBar = {
            TopAppBar(
                title = { Text(state.photo?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null -> Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = state.error)
                Spacer(Modifier.height(8.dp))
                FilledTonalButton(onClick = onRetry) { Text("Retry") }
            }

            state.photo != null ->
                AsyncImage(
                    model = ImageRequest
                        .Builder(context)
                        .data(state.photo.fullUrl)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = state.photo.title,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )

            else -> Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Photo not found.")
                Spacer(Modifier.height(8.dp))
                FilledTonalButton(onClick = onBack) { Text("Go back") }
            }
        }
    }
}

/* -------------------- PREVIEWS -------------------- */

private val previewPhoto = Photo(
    id = "123",
    title = "Golden Hour Overlook",
    thumbnailUrl = "https://live.staticflickr.com/65535/12345_q.jpg",
    fullUrl = "https://live.staticflickr.com/65535/12345_b.jpg"
)

@Preview(showBackground = true, name = "Detail — Loaded")
@Composable
private fun PhotoDetail_Loaded_Preview() {
    PhotoDetailContent(
        state = DetailState(photo = previewPhoto, isLoading = false, error = null),
        onBack = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Detail — Loading")
@Composable
private fun PhotoDetail_Loading_Preview() {
    PhotoDetailContent(
        state = DetailState(photo = null, isLoading = true, error = null),
        onBack = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Detail — Error")
@Composable
private fun PhotoDetail_Error_Preview() {
    PhotoDetailContent(
        state = DetailState(photo = null, isLoading = false, error = "Network error"),
        onBack = {},
        onRetry = {}
    )
}
