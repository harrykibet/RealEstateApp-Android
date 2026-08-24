package com.estatia.realestate.apps.feature.chats

import com.estatia.realestate.apps.core.testing.assertions.assertState
import kotlinx.coroutines.test.runTest
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
        viewModel.uiState.assertState {
            val current = this
            current is ChatUiState.Success && current.activeUsers.size == 5 && current.chats.size == 3
        }
    }
}
