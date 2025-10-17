package com.abhi.omadagallery.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.core.mapToDisplayError
import com.abhi.omadagallery.domain.usecase.LoadPhotoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val loadPhotoDetail: LoadPhotoDetail,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["id"])
    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            loadPhotoDetail(photoId).collect { ui ->
                when (ui) {
                    UiState.Loading -> _state.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is UiState.Success -> _state.update {
                        it.copy(
                            isLoading = false,
                            photo = ui.data
                        )
                    }

                    is UiState.Error -> _state.update {
                        it.copy(
                            isLoading = false,
                            error = mapToDisplayError(ui.message)
                        )
                    }
                }
            }
        }
    }
}
