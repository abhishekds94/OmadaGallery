package com.abhi.omadagallery.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val fullUrl: String
) : Parcelable
