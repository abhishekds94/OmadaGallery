package com.abhi.omadagallery.domain.model

data class PhotoPage(
    val photos: List<Photo>,
    val page: Int,
    val totalPages: Int
)
