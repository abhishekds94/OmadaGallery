package com.abhi.omadagallery.data.mappers

import com.abhi.omadagallery.data.remote.dto.PhotoDto
import com.abhi.omadagallery.data.remote.dto.PhotoInfoDto
import com.abhi.omadagallery.domain.model.Photo

private fun buildThumb(server: String, id: String, secret: String) =
    "https://live.staticflickr.com/$server/${id}_${secret}_q.jpg"

private fun buildFull(server: String, id: String, secret: String) =
    "https://live.staticflickr.com/$server/${id}_${secret}_b.jpg"

private fun toDomainCore(
    id: String, title: String, server: String, secret: String
) = Photo(
    id = id,
    title = title,
    thumbnailUrl = buildThumb(server, id, secret),
    fullUrl = buildFull(server, id, secret)
)

fun PhotoDto.toDomain(): Photo =
    toDomainCore(id = id, title = title, server = server, secret = secret)

fun PhotoInfoDto.toDomain(): Photo =
    toDomainCore(id = id, title = title.content, server = server, secret = secret)
