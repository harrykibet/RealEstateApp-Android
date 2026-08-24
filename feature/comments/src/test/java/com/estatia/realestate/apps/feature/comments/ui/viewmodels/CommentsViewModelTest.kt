package com.estatia.realestate.apps.feature.comments.ui.viewmodels

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.domain.repository.ICommentsRepository
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.core.testing.assertions.assertState
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
        viewModel.state.assertProperty("Nice place!") { input }
    }

    @Test
    fun `Load action starts observing comments successfully`() = runTest {
        val propertyId = "prop_1"
        every { commentsRepository.observeComments(propertyId) } returns flowOf(AppResult.Success(emptyList()))

        viewModel.onAction(CommentsAction.Load(propertyId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.assertState { 
            comments.isEmpty() && !isLoading
        }
    }

    @Test
    fun `Load action handles remote failure gracefully`() = runTest {
        val propertyId = "prop_fail"
        // 🧪 Scenario: Network goes down during comment observation
        every { commentsRepository.observeComments(propertyId) } returns flowOf(AppResult.Error(NetworkException.NoInternet))

        viewModel.onAction(CommentsAction.Load(propertyId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.assertState { 
            error == "No internet connection" && !isLoading
        }
    }

    @Test
    fun `SendComment success clears input and emits event`() = runTest {
        val propertyId = "prop_1"
        val message = "Great property"
        
        every { commentsRepository.observeComments(propertyId) } returns flowOf(AppResult.Success(emptyList()))
        viewModel.onAction(CommentsAction.Load(propertyId))
        viewModel.onAction(CommentsAction.InputChanged(message))
        
        coEvery { commentsRepository.submitComment(propertyId, message) } returns AppResult.Success(Unit)

        viewModel.events.test {
            viewModel.onAction(CommentsAction.SendComment)
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assert(event is CommentsEvent.ShowMessage)
            viewModel.state.assertProperty("") { input }
        }
    }
}
