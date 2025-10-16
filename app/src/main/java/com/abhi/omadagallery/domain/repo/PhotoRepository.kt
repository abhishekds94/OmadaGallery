package com.abhi.omadagallery.domain.repo

import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.domain.model.PhotoPage

interface PhotoRepository {
    suspend fun recent(page: Int): PhotoPage
    suspend fun search(query: String, page: Int): PhotoPage
    suspend fun photoById(id: String): Photo
}
