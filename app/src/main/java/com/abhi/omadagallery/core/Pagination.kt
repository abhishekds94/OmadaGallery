package com.abhi.omadagallery.core

import com.abhi.omadagallery.domain.model.Photo

// Comments (Documentation) here are generated using Gemini AI for Android Studio

// A utility object for handling common pagination logic like calculating pages and merging lists.
object Pagination {

    // Calculates the next page number based on the current page and a reset flag.
    fun getNextPage(isReset: Boolean, currentPage: Int): Int = when {
        isReset -> 1
        currentPage <= 0 -> 1
        else -> currentPage + 1
    }

    // Merges the current photo list with a new one, removing duplicates by photo ID.
    infix fun List<Photo>.mergedWith(newPhotos: List<Photo>): List<Photo> =
        (this + newPhotos).distinctBy { it.id }

    // Checks if the current page has reached or surpassed the total number of pages.
    infix fun Int.hasReachedEnd(totalPages: Int): Boolean = this >= totalPages

    // Determines if the next page can be loaded based on the current loading state, end-of-data status, and item presence.
    fun canLoadNextPage(isLoading: Boolean, hasReachedEnd: Boolean, hasItems: Boolean): Boolean =
        !isLoading && !hasReachedEnd && hasItems
}
