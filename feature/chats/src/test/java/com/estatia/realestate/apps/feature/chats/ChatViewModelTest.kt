package com.estatia.realestate.apps.feature.chats

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel()
    }

    @Test
    fun `initial state should eventually be Success with mock data`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state is ChatUiState.Success)
        val successState = state as ChatUiState.Success
        assertEquals(5, successState.activeUsers.size)
        assertEquals(3, successState.chats.size)
    }
}
