package com.abhi.omadagallery.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhi.omadagallery.core.Pagination
import com.abhi.omadagallery.core.Pagination.hasReachedEnd
import com.abhi.omadagallery.core.Pagination.mergedWith
import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.domain.model.PhotoPage
import com.abhi.omadagallery.domain.usecase.LoadPhotos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val loadPhotos: LoadPhotos
) : ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state

    private val _effects = Channel<GalleryEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var loadingJob: Job? = null

    fun handleIntent(intent: GalleryIntent) = when (intent) {
        GalleryIntent.LoadInitial -> loadPage(true)
        is GalleryIntent.Search -> loadPage(true, intent.query)
        GalleryIntent.LoadNextPage -> tryLoadNext()
        GalleryIntent.Retry -> loadPage(false)
    }

    private fun tryLoadNext() = state.value.let { it ->
        if (Pagination.canLoadNextPage(
                it.isLoading,
                it.endReached,
                it.items.isNotEmpty()
            )
        )
            loadPage(false)
    }

    private fun loadPage(reload: Boolean, searchQuery: String? = null) {
        if (loadingJob?.isActive == true) return
        val s = state.value
        val query = searchQuery?.takeIf { it.isNotBlank() } ?: s.query
        val pageToLoad = Pagination.getNextPage(reload, s.page)

        _state.update {
            it.copy(
                query = query,
                isLoading = true,
                error = null,
                items = if (reload) emptyList() else it.items,
                endReached = if (reload) false else it.endReached
            )
        }

        loadingJob = viewModelScope.launch {
            loadPhotos(query, pageToLoad).collect { ui ->
                when (ui) {
                    UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> applySuccess(ui.data, reload)
                    is UiState.Error -> applyError(ui.message)
                }
            }
        }
    }

    private fun applySuccess(photoPage: PhotoPage, isReset: Boolean) {
        _state.update { it ->
            val merged = if (isReset) {
                photoPage.photos
            } else {
                it.items mergedWith photoPage.photos
            }

            it.copy(
                items = merged,
                page = photoPage.page.coerceAtLeast(1),
                totalPages = photoPage.totalPages.coerceAtLeast(1),
                isLoading = false,
                endReached = photoPage.page hasReachedEnd photoPage.totalPages,
                error = null
            )
        }
    }

    private suspend fun applyError(message: String) {
        val hadItems = state.value.items.isNotEmpty()
        _state.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
        if (hadItems) {
            _effects.send(GalleryEffect.ShowMessage(message, "Retry"))
        }
    }
}
