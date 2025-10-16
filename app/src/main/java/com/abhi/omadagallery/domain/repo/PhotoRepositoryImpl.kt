package com.abhi.omadagallery.domain.repo

import com.abhi.omadagallery.core.Keys
import com.abhi.omadagallery.data.mappers.toDomain
import com.abhi.omadagallery.data.remote.FlickrApi
import com.abhi.omadagallery.data.remote.dto.PhotosResponseDto
import com.abhi.omadagallery.domain.model.PhotoPage
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

    private suspend fun fetchPhotos(apiCall: suspend () -> PhotosResponseDto): PhotoPage {
        val response = apiCall()
        val photos = response.photos.photo.map { it.toDomain() }
        return PhotoPage(
            photos = photos.distinctBy { it.id },
            page = response.photos.page,
            totalPages = response.photos.pages
        )
    }
}
