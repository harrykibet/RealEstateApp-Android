package com.application.real_estate_app

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.application.real_estate_app.R
import com.application.real_estate_app.core.common.events.LoginEvent
import com.application.real_estate_app.core.common.events.LogoutEvent
import com.application.real_estate_app.core.common.misc.Consts
import com.application.real_estate_app.feature_auth.ui.viewModels.AuthViewModel
import com.application.real_estate_app.ui.activities.MainActivity
import com.application.real_estate_app.utils.FireStoreConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.mockk.*
import org.greenrobot.eventbus.EventBus
import org.junit.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@ExtendWith(InstantTaskExecutorRule::class)
@Config(sdk = [Build.VERSION_CODES.S]) // Simulating Android 12+
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private lateinit var activity: MainActivity
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel
    private lateinit var bottomNavigationView: BottomNavigationView

    @Before
    fun setup() {
        // Mock dependencies
        navController = mockk(relaxed = true)
        authViewModel = mockk(relaxed = true)

        // Mock ViewModel behavior
        every { authViewModel.isUserLoggedIn } returns MutableLiveData(false)
        every { authViewModel.isAuthCheckComplete() } returns true
        every { authViewModel.checkAuthentication() } just Runs

        // Create the Activity
        activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        // Inject mocks into activity
        activity.navController = navController
        activity.authViewModel = authViewModel

        // Mock bottom navigation
        bottomNavigationView = activity.findViewById(R.id.bottomNavigation)
    }

    @Test
    fun `should navigate to home when user is authenticated`() {
        // Simulate authentication
        every { authViewModel.isUserLoggedIn } returns MutableLiveData(true)

        // Call method
        activity.navigateBasedOnAuthentication(true)

        // Verify navigation and UI state
        assertEquals(View.VISIBLE, bottomNavigationView.visibility)
        verify { navController.popBackStack(R.id.feature_auth_nav_graph, true) }
        verify { navController.navigate(R.id.feature_home_nav_graph) }
    }

    @Test
    fun `should navigate to login when user is not authenticated`() {
        // Simulate authentication failure
        every { authViewModel.isUserLoggedIn } returns MutableLiveData(false)

        // Call method
        activity.navigateBasedOnAuthentication(false)

        // Verify navigation and UI state
        assertEquals(View.GONE, bottomNavigationView.visibility)
        verify { navController.popBackStack(R.id.feature_home_nav_graph, true) }
        verify { navController.navigate(R.id.feature_auth_nav_graph) }
    }

    @Test
    fun `should handle onNewIntent and deep link`() {
        val intent = mockk<Intent>(relaxed = true)

        activity.onNewIntent(intent)

        verify { navController.handleDeepLink(intent) }
    }

    @Test
    fun `should handle login event and navigate to home`() {
        activity.onLoginEvent(LoginEvent())

        verify { navController.popBackStack(R.id.feature_auth_nav_graph, true) }
        verify { navController.navigate(R.id.feature_home_nav_graph) }
    }

    @Test
    fun `should handle logout event and navigate to login`() {
        activity.onLogoutEvent(LogoutEvent())

        verify { navController.navigate(R.id.feature_auth_nav_graph) }
    }

    @Test
    fun `should register and unregister EventBus correctly`() {
        mockkStatic(EventBus::class)

        val eventBus = mockk<EventBus>(relaxed = true)
        every { EventBus.getDefault() } returns eventBus

        activity.onStart()
        verify { eventBus.register(activity) }

        activity.onStop()
        verify { eventBus.unregister(activity) }
    }

    @Test
    fun `should show toast on authentication status`() {
        val toast = Shadows.shadowOf(Toast.makeText(activity, "Authenticated", Toast.LENGTH_SHORT))
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Authenticated", toast.text)
    }
}
