package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosPageDto(
    @SerialName("page") val page: Int,
    @SerialName("pages") val pages: Int,
    @SerialName("perpage") val perPage: Int,
    @SerialName("photo") val photo: List<PhotoDto>
)
