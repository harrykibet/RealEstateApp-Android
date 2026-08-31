package com.estatia.realestate.apps.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a Baseline Profile to improve the app's performance.
 * Captures critical user journeys (CUJs) including conditional startup and deep navigation.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName,
        includeInStartupProfile = true
    ) {
        // 1. Startup: Launch the app
        pressHome()
        startActivityAndWait()

        // 2. Handle Conditional Auth State
        // Check if we land on the Login screen (Unauthenticated)
        val loginButton = device.findObject(By.res("LoginButton"))
        if (loginButton != null) {
            // UN-AUTHENTICATED JOURNEY
            
            // Capture Login field interaction
            val emailField = device.findObject(By.res("LoginEmailField"))
            emailField?.text = "user@estatia.com"
            
            val passwordField = device.findObject(By.res("LoginPasswordField"))
            passwordField?.text = "password"
            
            // Perform Login to access the authenticated tabs
            loginButton.click()
            
            // Wait for Home screen to load (PropertyFeed tag indicates successful login)
            device.wait(Until.findObject(By.res("PropertyFeed")), 10000)
        }

        // 3. AUTHENTICATED JOURNEY (Home, Feed, Search, Tabs)
        val feed = device.findObject(By.res("PropertyFeed"))
        if (feed != null) {
            // A. Home Feed: Scroll through vertical feed (video playback & rendering)
            feed.setGestureMargin(device.displayWidth / 10)
            feed.fling(Direction.DOWN)
            device.waitForIdle()
            feed.fling(Direction.DOWN)
            device.waitForIdle()

            // B. Search Journey: Click search icon in TopAppBar and interact
            val searchIcon = device.findObject(By.res("EstatiaTopAppBarNavIcon"))
            if (searchIcon != null) {
                searchIcon.click()
                device.waitForIdle()
                
                val searchField = device.wait(Until.findObject(By.res("SearchTextField")), 5000)
                if (searchField != null) {
                    searchField.text = "Nairobi"
                    device.pressEnter()
                    device.waitForIdle()
                }
                
                // Return to feed
                device.pressBack()
                device.waitForIdle()
            }

            // C. Details Journey: Horizontal swipe in feed (TikTok style)
            feed.swipe(Direction.LEFT, 1.0f)
            device.waitForIdle()
            
            // Return to feed
            device.pressBack()
            device.waitForIdle()
        }

        // D. Navigation: Cycle through bottom navigation tabs
        val navItems = device.findObjects(By.res("EstatiaNavItem"))
        // Tabs: [HOME, ADD_PROPERTY, CHATS, MARKET, PROFILE]
        
        if (navItems.size >= 5) {
            // Market Tab
            navItems[3].click()
            device.waitForIdle()

            // Chats Tab
            navItems[2].click()
            device.waitForIdle()

            // Add Property Tab
            navItems[1].click()
            device.waitForIdle()

            // Profile Tab (No login button here when authenticated)
            navItems[4].click()
            device.waitForIdle()
        }
        
        // Final return to Home
        if (navItems.isNotEmpty()) {
            navItems[0].click()
            device.waitForIdle()
        }
    }
}
