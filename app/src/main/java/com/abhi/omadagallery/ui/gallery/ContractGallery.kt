package com.abhi.omadagallery.ui.gallery

import com.abhi.omadagallery.domain.model.Photo

sealed interface GalleryIntent {
    data object LoadInitial : GalleryIntent
    data class Search(val query: String?) : GalleryIntent
    data object LoadNextPage : GalleryIntent
    data object Retry : GalleryIntent
}

data class GalleryState(
    val query: String? = null,
    val items: List<Photo> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = items.isEmpty() && !isLoading && error == null
}

sealed interface GalleryEffect {
    data class ShowMessage(val message: String, val actionLable: String? = null) : GalleryEffect
}