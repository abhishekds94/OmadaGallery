package com.abhi.omadagallery.domain.usecase

import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.domain.model.PhotoPage
import com.abhi.omadagallery.domain.repo.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadPhotos @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(query: String?, page: Int): Flow<UiState<PhotoPage>> = flow {
        emit(UiState.Loading)

        val data = if (query.isNullOrBlank()) {
            repo.recent(page)
        } else {
            repo.search(query, page)
        }

        emit(UiState.Success(data))

    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown error", e))
    }
}
