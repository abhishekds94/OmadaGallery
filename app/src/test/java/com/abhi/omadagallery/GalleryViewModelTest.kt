package com.abhi.omadagallery

import app.cash.turbine.test
import com.abhi.omadagallery.core.UiState
import com.abhi.omadagallery.domain.model.Photo
import com.abhi.omadagallery.domain.model.PhotoPage
import com.abhi.omadagallery.domain.usecase.LoadPhotos
import com.abhi.omadagallery.ui.gallery.GalleryEffect
import com.abhi.omadagallery.ui.gallery.GalleryIntent
import com.abhi.omadagallery.ui.gallery.GalleryViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val loadPhotos: LoadPhotos = mockk(relaxed = true)

    private fun vm() = GalleryViewModel(loadPhotos)

    private fun photo(id: String) = Photo(
        id = id,
        title = "t$id",
        thumbnailUrl = "thumb/$id",
        fullUrl = "full/$id"
    )

    private fun page(
        ids: List<String>,
        page: Int,
        total: Int
    ) = PhotoPage(photos = ids.map(::photo), page = page, totalPages = total)

    @Test
    fun `LoadInitial success populates first page and clears loading`() = runTest {
        coEvery { loadPhotos(null, 1) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Success(page(listOf("1", "2", "3"), page = 1, total = 5)))
        }

        val vm = vm()
        vm.handleIntent(GalleryIntent.LoadInitial)

        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(false, s.isLoading)
        assertEquals(1, s.page)
        assertEquals(5, s.totalPages)
        assertEquals(listOf("1", "2", "3"), s.items.map { it.id })
        assertEquals(null, s.error)
        assertEquals(false, s.endReached)
    }

    @Test
    fun `Search resets items and sets query`() = runTest {
        coEvery { loadPhotos("cats", 1) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Success(page(listOf("10", "11"), page = 1, total = 2)))
        }

        val vm = vm()
        vm.handleIntent(GalleryIntent.Search("cats"))
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals("cats", s.query)
        assertEquals(listOf("10", "11"), s.items.map { it.id })
        assertEquals(1, s.page)
        assertEquals(2, s.totalPages)
        assertEquals(false, s.isLoading)
        assertEquals(false, s.endReached)
    }

    @Test
    fun `Error on next page emits snackbar effect with Retry`() = runTest {
        // First page ok
        coEvery { loadPhotos(null, 1) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Success(page(listOf("1", "2"), page = 1, total = 3)))
        }
        // Next page fails
        coEvery { loadPhotos(null, 2) } returns flow {
            emit(UiState.Loading)
            emit(UiState.Error("Timeout"))
        }

        val vm = vm()

        vm.effects.test {
            vm.handleIntent(GalleryIntent.LoadInitial)
            advanceUntilIdle()

            vm.handleIntent(GalleryIntent.LoadNextPage)
            advanceUntilIdle()

            val s = vm.state.value
            assertEquals(false, s.isLoading)
            assertEquals("Timeout", s.error)
            assertEquals(listOf("1", "2"), s.items.map { it.id })

            val effect = awaitItem()
            require(effect is GalleryEffect.ShowMessage)
            assertEquals("Timeout", effect.message)
            assertEquals("Retry", effect.actionLable)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
