package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotosPageDto(
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val photo: List<PhotoDto>
)
