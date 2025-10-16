package com.abhi.omadagallery.ui.gallery

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.abhi.omadagallery.domain.model.Photo

@Composable
fun GalleryScreen(
    onOpen: (Photo) -> Unit
) {
    Scaffold(
        topBar = { /* SearchBar */ },
        snackbarHost = {
            SnackbarHost(
                remember {
                    SnackbarHostState()
                }
            )
        }
    ) {
        Text(
            text = "Gallery Screen",
            modifier = Modifier
                .padding(it)
        )
    }
}
