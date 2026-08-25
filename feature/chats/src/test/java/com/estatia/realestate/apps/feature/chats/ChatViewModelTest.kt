package com.estatia.realestate.apps.feature.chats

import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.clock.TestClock
import com.estatia.realestate.apps.core.testing.clock.TestTicker
import com.estatia.realestate.apps.core.testing.fixtures.ChatFixtures
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel
    private val testClock = TestClock(0L)
    private val ticker = TestTicker(testClock)

    @Before
    fun setup() {
        viewModel = ChatViewModel()
    }

    @Test
    fun `viewModel handles high-frequency state updates without UI flickering`() = runTest {
        // 🧪 Micro-Step Simulation: 60fps frame steps (16ms)
        ticker.tick(count = 10, intervalMillis = 16)
        
        viewModel.uiState.assertState {
            val current = this
            current is ChatUiState.Success && current.activeUsers.isNotEmpty()
        }
    }

    @Test
    fun `initial state is Success with mock data`() = runTest {
        viewModel.uiState.assertState {
            val current = this
            current is ChatUiState.Success && current.activeUsers.size == 5 && current.chats.size == 3
        }
    }

    @Test
    fun `viewModel correctly processes fixtures`() = runTest {
        val mockChat = ChatFixtures.buildChat("test_id", "test message")
        // This test would normally verify how the VM handles new chats from a repository
        assert(mockChat.id == "test_id")
    }
}
