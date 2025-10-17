package com.abhi.omadagallery.core

import android.content.Context
import coil.ImageLoader

fun provideImageLoader(context: Context): ImageLoader =
    ImageLoader.Builder(context)
        .crossfade(true)
        .respectCacheHeaders(false)
        // .logger(DebugLogger())
        .build()