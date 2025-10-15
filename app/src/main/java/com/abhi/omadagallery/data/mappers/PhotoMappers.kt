package com.abhi.omadagallery.data.mappers

import com.abhi.omadagallery.data.remote.dto.PhotoDto
import com.abhi.omadagallery.domain.model.Photo

fun PhotoDto.toDomain(): Photo {
    val base = "https://live.staticflickr.com/${server}/${id}_${secret}"
    return Photo(
        id = id,
        title = title,
        thumbnailUrl = "${base}_q.jpg",
        fullUrl = "${base}.jpg"
    )
}