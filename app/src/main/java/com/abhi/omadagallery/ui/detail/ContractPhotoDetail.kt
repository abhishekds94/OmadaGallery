package com.abhi.omadagallery.ui.detail

import com.abhi.omadagallery.domain.model.Photo

data class DetailState(
    val photo: Photo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
