package com.abhi.omadagallery.domain.repo

import com.abhi.omadagallery.core.Keys
import com.abhi.omadagallery.data.mappers.toDomain
import com.abhi.omadagallery.data.remote.FlickrApi
import com.abhi.omadagallery.data.remote.dto.PhotosResponseDto
import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.domain.model.PhotoPage
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val api: FlickrApi
) : PhotoRepository {

    override suspend fun recent(page: Int): PhotoPage =
        fetchPhotos { api.getRecent(key = Keys.flickerKey, page = page) }

    override suspend fun search(query: String, page: Int): PhotoPage =
        fetchPhotos { api.search(key = Keys.flickerKey, text = query, page = page) }

    override suspend fun photoById(id: String): Photo {
        val res = api.getInfo(key = Keys.flickerKey, id = id)
        if (res.stat != "ok" || res.photo == null) {
            throw IOException(res.message ?: "Flickr photo info error")
        }
        return res.photo.toDomain()
    }

    private suspend fun fetchPhotos(
        call: suspend () -> PhotosResponseDto
    ): PhotoPage {
        val res = call()

        if (res.stat != "ok" || res.photos == null) {
            val msg = buildString {
                append(res.message ?: "Flickr photos error")
                res.code?.let { append(" (code $it)") }
            }.ifBlank { "Flickr photos error" }
            throw IOException(msg)
        }

        val pageDto = res.photos
        val items = pageDto.photo
            .map { it.toDomain() }
            .distinctBy { it.id }

        return PhotoPage(
            photos = items,
            page = pageDto.page.coerceAtLeast(1),
            totalPages = pageDto.pages.coerceAtLeast(1)
        )
    }
}
