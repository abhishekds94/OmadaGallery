package com.abhi.omadagallery.domain.usecase

import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.domain.repo.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoadPhotoDetail @Inject constructor(
    private val repo: PhotoRepository
) {
    operator fun invoke(id: String): Flow<UiState<Photo>> = flow {
        emit(UiState.Loading)
        emit(UiState.Success(repo.photoById(id)))
    }.catch { e ->
        emit(
            UiState.Error(
                e.message ?: "Unknown error", e
            )
        )
    }
}
