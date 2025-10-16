package com.abhi.omadagallery.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.abhi.omadagallery.domain.model.Photo

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
    onRetry: () -> Unit
) {
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
            state.isLoading -> LinearProgressIndicator(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
            )

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

            state.photo != null -> AsyncImage(
                model = state.photo.fullUrl,
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
