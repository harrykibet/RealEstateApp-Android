package com.application.real_estate_app


import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.application.real_estate_app.R
import com.application.real_estate_app.core.common.events.LoginEvent
import com.application.real_estate_app.core.common.events.LogoutEvent
import com.application.real_estate_app.feature_auth.ui.viewModels.AuthViewModel
import com.application.real_estate_app.ui.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.greenrobot.eventbus.EventBus
import org.junit.*
import org.junit.jupiter.api.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject() // Ensures dependency injection is set up before running tests
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun `should show bottom navigation when authenticated`() {
        activityRule.scenario.onActivity { activity ->
            activity.navigateBasedOnAuthentication(true)
        }

        onView(withId(R.id.bottomNavigation))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `should hide bottom navigation when not authenticated`() {
        activityRule.scenario.onActivity { activity ->
            activity.navigateBasedOnAuthentication(false)
        }

        onView(withId(R.id.bottomNavigation))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun `should navigate to home on login event`() {
        activityRule.scenario.onActivity { activity ->
            EventBus.getDefault().post(LoginEvent())
        }

        // Assert that MainActivity has navigated to HomeFragment (replace with actual ID)
        onView(withId(R.id.homeFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `should navigate to login on logout event`() {
        activityRule.scenario.onActivity { activity ->
            EventBus.getDefault().post(LogoutEvent())
        }

        onView(withId(R.id.feature_auth_nav_graph))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `should handle deep links with onNewIntent`() {
        val deepLinkIntent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        ).apply {
            action = Intent.ACTION_VIEW
        }

        activityRule.scenario.onActivity { activity ->
            activity.onNewIntent(deepLinkIntent)
        }

        Intents.intended(hasComponent(MainActivity::class.java.name))
    }
}
