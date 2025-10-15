package com.abhi.omadagallery.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String,
    val server: String,
    val secret: String,
    val title: String
)
