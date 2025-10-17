package com.abhi.omadagallery

import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.domain.usecase.LoadPhotoDetail
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import androidx.lifecycle.SavedStateHandle
import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.ui.detail.PhotoDetailViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val useCase: LoadPhotoDetail = mockk()

    private fun photo(id: String) = Photo(
        id = id,
        title = "Title $id",
        thumbnailUrl = "thumb/$id",
        fullUrl = "full/$id"
    )

    private fun vmWithId(id: String): PhotoDetailViewModel {
        val handle = SavedStateHandle(mapOf("id" to id))
        return PhotoDetailViewModel(loadPhotoDetail = useCase, savedStateHandle = handle)
    }

    @Test
    fun `init load success updates state with photo`() = runTest {
        val id = "42"
        val p = photo(id)

        // Flow: Loading -> Success
        coEvery { useCase.invoke(id) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Success(p))
        }

        val vm = vmWithId(id)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(false, s.isLoading)
        assertNull(s.error)
        assertEquals(p, s.photo)
        assertEquals("42", s.photo?.id)
    }

    @Test
    fun `init load error updates state with error`() = runTest {
        val id = "99"

        // Flow: Loading -> Error
        coEvery { useCase.invoke(id) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Error("boom"))
        }

        val vm = vmWithId(id)
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(false, s.isLoading)
        assertEquals("boom", s.error)
        assertNull(s.photo)
    }
}
