package com.application.real_estate_app

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.application.real_estate_app.core_common.events.LoginEvent
import com.application.real_estate_app.core_common.events.LogoutEvent
import com.application.real_estate_app.ui.activities.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.greenrobot.eventbus.EventBus
import org.junit.*
import org.junit.rules.TestRule

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule: TestRule = androidx.arch.core.executor.testing.InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject() // Inject dependencies before test execution
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun shouldShowBottomNavigationWhenAuthenticated() {
        activityRule.scenario.onActivity { activity ->
            activity.navigateBasedOnAuthentication(true)
        }

        onView(withId(R.id.bottomNavigation))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldHideBottomNavigationWhenNotAuthenticated() {
        activityRule.scenario.onActivity { activity ->
            activity.navigateBasedOnAuthentication(false)
        }

        onView(withId(R.id.bottomNavigation))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun shouldNavigateToHomeOnLoginEvent() {
        activityRule.scenario.onActivity {
            EventBus.getDefault().post(LoginEvent())
        }

        // Ensure navigation to HomeFragment
        onView(withId(R.id.homeFragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldNavigateToLoginOnLogoutEvent() {
        activityRule.scenario.onActivity {
            EventBus.getDefault().post(LogoutEvent())
        }

        // Ensure navigation to login fragment
        onView(withId(com.application.real_estate_app.feature_auth.R.id.feature_auth_nav_graph))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldHandleDeepLinksWithOnNewIntent() {
        val deepLinkIntent = Intent(
            ApplicationProvider.getApplicationContext(),
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
