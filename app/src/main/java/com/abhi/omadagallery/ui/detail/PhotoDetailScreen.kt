package com.abhi.omadagallery.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhi.omadagallery.R
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
    Column(Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 0.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
        if (state.photo != null) {
            AutoSizeImage(
                url = state.photo.fullUrl,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading)
                    CircularProgressIndicator()
                else
                    Text(stringResource(R.string.photo_not_available))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        ) {
            Text(
                text = state.photo?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge
            )

            when {
                state.isLoading -> {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.loading_details))
                    }
                }

                state.error != null -> {
                    Spacer(Modifier.height(12.dp))
                    Text(state.error, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    FilledTonalButton(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
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
