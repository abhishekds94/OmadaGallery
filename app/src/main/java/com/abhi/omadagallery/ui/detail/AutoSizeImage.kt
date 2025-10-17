package com.abhi.omadagallery.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun AutoSizeImage(
    url: String, modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(model = url)
    val state = painter.state

    var ratio by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(state) {
        if (state is AsyncImagePainter.State.Success) {
            val d = state.result.drawable
            val w = d.intrinsicWidth
            val h = d.intrinsicHeight
            if (w > 0 && h > 0) ratio = w.toFloat() / h.toFloat()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (ratio != null) Modifier.aspectRatio(ratio!!) else Modifier.wrapContentHeight()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        if (state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator()
        }
    }
}
