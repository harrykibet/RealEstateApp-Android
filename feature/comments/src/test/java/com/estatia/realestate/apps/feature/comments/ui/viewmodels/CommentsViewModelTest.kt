package com.estatia.realestate.apps.feature.comments.ui.viewmodels

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.events.CommentsEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommentsViewModelTest {

    private lateinit var commentsRepository: ICommentsRepository
    private lateinit var viewModel: CommentsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        commentsRepository = mockk()
        viewModel = CommentsViewModel(commentsRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `InputChanged updates input state`() = runTest {
        viewModel.onAction(CommentsAction.InputChanged("Nice place!"))
        assertEquals("Nice place!", viewModel.state.value.input)
    }

    @Test
    fun `Load action starts observing comments`() = runTest {
        val propertyId = "prop_1"
        val mockComments = emptyList<CommentDomainModel>()
        every { commentsRepository.observeComments(propertyId) } returns flowOf(AppResult.Success(mockComments))

        viewModel.onAction(CommentsAction.Load(propertyId))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockComments, viewModel.state.value.comments)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `SendComment success clears input and emits event`() = runTest {
        val propertyId = "prop_1"
        val message = "Great property"
        
        // Prepare state
        every { commentsRepository.observeComments(propertyId) } returns flowOf(AppResult.Success(emptyList()))
        viewModel.onAction(CommentsAction.Load(propertyId))
        viewModel.onAction(CommentsAction.InputChanged(message))
        
        coEvery { commentsRepository.submitComment(propertyId, message) } returns AppResult.Success(Unit)

        viewModel.events.test {
            // When
            viewModel.onAction(CommentsAction.SendComment)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val event = awaitItem()
            assert(event is CommentsEvent.ShowMessage)
            assertEquals("Comment posted", (event as CommentsEvent.ShowMessage).message)
            assertEquals("", viewModel.state.value.input)
        }
    }
}
