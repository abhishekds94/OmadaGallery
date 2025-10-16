package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    @SerialName("_content") val content: String
)
