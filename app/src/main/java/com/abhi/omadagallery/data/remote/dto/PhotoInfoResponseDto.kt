package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoInfoResponseDto(
    @SerialName("photo") val photo: PhotoInfoDto? = null,
    @SerialName("stat") val stat: String,
    @SerialName("code") val code: Int? = null,
    @SerialName("message") val message: String? = null
)
