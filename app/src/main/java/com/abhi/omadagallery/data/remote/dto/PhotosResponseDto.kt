package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseDto(
    val photos: PhotosPageDto,
    val stat: String
)