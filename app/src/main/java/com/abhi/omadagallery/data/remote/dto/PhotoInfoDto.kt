package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoInfoDto(
    @SerialName("id") val id: String,
    @SerialName("server") val server: String,
    @SerialName("secret") val secret: String,
    @SerialName("title") val title: ContentDto
)
