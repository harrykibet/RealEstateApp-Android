package com.estatia.realestate.apps.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the Baseline Profile for Estatia's critical user journeys.
 *
 * Authentication is intentionally NOT performed by this benchmark.
 *
 * The application must already be in an authenticated state before the
 * authenticated journeys are executed. This prevents credentials from being
 * embedded in benchmark source code and avoids coupling profile generation
 * to a particular backend account.
 *
 * The benchmark is fail-fast. Required UI elements are asserted rather than
 * silently skipped so that an incomplete journey cannot accidentally produce
 * an incomplete Baseline Profile.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .packageName,
        includeInStartupProfile = true,
    ) {
        val device = UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation(),
        )

        // ---------------------------------------------------------------------
        // Startup
        // ---------------------------------------------------------------------

        pressHome()
        startActivityAndWait()

        /*
         * Baseline Profile generation must operate on an authenticated
         * application session.
         *
         * We deliberately do not attempt to log in here.
         */
        waitForAuthenticatedApplication(device)

        // ---------------------------------------------------------------------
        // Critical User Journeys
        // ---------------------------------------------------------------------

        exerciseHomeFeed(device)

        exerciseSearch(device)

        exercisePropertyNavigation(device)

        exerciseTopLevelDestinations(device)

        // Always finish on Home so the benchmark ends in a deterministic state.
        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.HOME,
        )

        waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Home property feed",
        )

        device.waitForIdle(UI_IDLE_TIMEOUT_MS)
    }

    // =========================================================================
    // Authentication
    // =========================================================================

    /**
     * Verifies that the benchmark started with an authenticated session.
     *
     * This method intentionally does not perform authentication.
     *
     * Credentials must never be embedded in BaselineProfileGenerator.
     */
    private fun waitForAuthenticatedApplication(
        device: UiDevice,
    ) {
        val homeNavigationItem = device.wait(
            Until.findObject(
                By.res(NavigationTestTags.HOME),
            ),
            AUTHENTICATION_TIMEOUT_MS,
        )

        assertNotNull(
            """
            Baseline Profile generation requires an authenticated Estatia session.

            The authenticated navigation UI was not detected within
            ${AUTHENTICATION_TIMEOUT_MS / 1_000} seconds.

            Authenticate the benchmark application before running the
            Baseline Profile generator.

            BaselineProfileGenerator intentionally does not contain credentials
            or perform authentication.
            """.trimIndent(),
            homeNavigationItem,
        )
    }

    // =========================================================================
    // Home
    // =========================================================================

    /**
     * Exercises the primary property feed.
     *
     * The property feed is one of the application's primary performance paths
     * because it combines Compose rendering, scrolling, property content and
     * potentially media playback.
     */
    private fun exerciseHomeFeed(
        device: UiDevice,
    ) {
        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.HOME,
        )

        val feed = waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Home property feed",
        )

        feed.setGestureMargin(device.displayWidth / 10)

        feed.fling(Direction.DOWN)
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        feed.fling(Direction.DOWN)
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)
    }

    // =========================================================================
    // Search
    // =========================================================================

    /**
     * Exercises the application's search journey from Home.
     */
    private fun exerciseSearch(
        device: UiDevice,
    ) {
        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.HOME,
        )

        waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Property feed before opening Search",
        )

        val searchButton = waitFor(
            device = device,
            tag = ScreenTestTags.TOP_APP_BAR_NAVIGATION,
            description = "Home search navigation button",
        )

        searchButton.click()

        val searchField = waitFor(
            device = device,
            tag = ScreenTestTags.SEARCH_FIELD,
            description = "Search text field",
        )

        searchField.text = SEARCH_QUERY

        device.pressEnter()

        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        /*
         * Search is a separate destination. Return to Home rather than
         * assuming a particular navigation implementation.
         */
        device.pressBack()

        waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Property feed after returning from Search",
        )
    }

    // =========================================================================
    // Property Details
    // =========================================================================

    /**
     * Exercises property interaction from the vertical property feed.
     *
     * This follows the existing application's feed interaction rather than
     * inventing benchmark-only UI.
     */
    private fun exercisePropertyNavigation(
        device: UiDevice,
    ) {
        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.HOME,
        )

        val feed = waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Property feed before property interaction",
        )

        /*
         * Estatia's property feed uses gesture-driven content.
         *
         * Keep this interaction deliberately small and deterministic. The
         * objective is to exercise the code path, not perform an exhaustive
         * UI test.
         */
        feed.swipe(
            Direction.LEFT,
            PROPERTY_SWIPE_PERCENT,
        )

        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        device.pressBack()

        waitFor(
            device = device,
            tag = ScreenTestTags.PROPERTY_FEED,
            description = "Property feed after returning from property details",
        )
    }

    // =========================================================================
    // Top-Level Navigation
    // =========================================================================

    /**
     * Exercises every authenticated top-level destination.
     *
     * Navigation is identified by semantic test tags instead of positional
     * indexes such as navItems[0] or navItems[4].
     *
     * TopLevelDestination is the application's source of truth for these
     * destinations.
     */
    private fun exerciseTopLevelDestinations(
        device: UiDevice,
    ) {
        // ---------------------------------------------------------------------
        // Market
        // ---------------------------------------------------------------------

        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.MARKET,
        )

        waitFor(
            device = device,
            tag = ScreenTestTags.MARKET,
            description = "Market screen",
        )

        // ---------------------------------------------------------------------
        // Chats
        // ---------------------------------------------------------------------

        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.CHATS,
        )

        waitFor(
            device = device,
            tag = ScreenTestTags.CHATS,
            description = "Chats screen",
        )

        // ---------------------------------------------------------------------
        // Add Property
        // ---------------------------------------------------------------------

        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.ADD_PROPERTY,
        )

        waitFor(
            device = device,
            tag = ScreenTestTags.ADD_PROPERTY,
            description = "Add Property screen",
        )

        // ---------------------------------------------------------------------
        // Profile
        // ---------------------------------------------------------------------

        navigateTo(
            device = device,
            navigationTag = NavigationTestTags.PROFILE,
        )

        exerciseProfile(device)
    }

    // =========================================================================
    // Profile
    // =========================================================================

    /**
     * Exercises the actual ProfileScreen UI.
     *
     * There is intentionally NO authentication/login interaction here.
     *
     * The authenticated ProfileScreen contains:
     *
     * - Profile information
     * - Statistics
     * - Edit Profile
     * - Share
     * - Listings
     * - Favorites
     * - Reviews
     *
     * The profile content tabs are therefore the appropriate representative
     * Profile CUJ for this Baseline Profile.
     */
    private fun exerciseProfile(
        device: UiDevice,
    ) {
        waitFor(
            device = device,
            tag = ScreenTestTags.PROFILE,
            description = "Profile screen",
        )

        val listingsTab = waitFor(
            device = device,
            tag = ProfileTestTags.LISTINGS,
            description = "Profile Listings tab",
        )

        val favoritesTab = waitFor(
            device = device,
            tag = ProfileTestTags.FAVORITES,
            description = "Profile Favorites tab",
        )

        val reviewsTab = waitFor(
            device = device,
            tag = ProfileTestTags.REVIEWS,
            description = "Profile Reviews tab",
        )

        // Listings
        listingsTab.click()
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        // Favorites
        favoritesTab.click()
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        // Reviews
        reviewsTab.click()
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)

        // Restore the default Profile tab.
        listingsTab.click()
        device.waitForIdle(UI_IDLE_TIMEOUT_MS)
    }

    // =========================================================================
    // Navigation Helpers
    // =========================================================================

    /**
     * Navigates to a top-level destination using its semantic test tag.
     *
     * We intentionally avoid relying on the order of TopLevelDestination.entries
     * or the index of elements returned by UiDevice.findObjects().
     */
    private fun navigateTo(
        device: UiDevice,
        navigationTag: String,
    ) {
        val navigationItem = waitFor(
            device = device,
            tag = navigationTag,
            description = "Navigation destination: $navigationTag",
        )

        navigationItem.click()

        device.waitForIdle(UI_IDLE_TIMEOUT_MS)
    }

    // =========================================================================
    // UI Synchronization
    // =========================================================================

    /**
     * Waits for a required UI element and fails the benchmark if it does not
     * appear within the timeout.
     *
     * Returning nullable UI objects and silently continuing is intentionally
     * avoided because that can generate an incomplete Baseline Profile.
     */
    private fun waitFor(
        device: UiDevice,
        tag: String,
        description: String,
        timeoutMs: Long = UI_TIMEOUT_MS,
    ): UiObject2 {
        val object2 = device.wait(
            Until.findObject(By.res(tag)),
            timeoutMs,
        )

        assertNotNull(
            """
            Required benchmark UI was not found.

            Description: $description
            Resource ID: $tag
            Timeout: ${timeoutMs / 1_000}s
            """.trimIndent(),
            object2,
        )

        return object2
    }

    private companion object {
        const val AUTHENTICATION_TIMEOUT_MS = 60_000L

        const val UI_TIMEOUT_MS = 15_000L

        const val UI_IDLE_TIMEOUT_MS = 5_000L

        const val SEARCH_QUERY = "Nairobi"

        const val PROPERTY_SWIPE_PERCENT = 1.0f
    }
}

/**
 * Stable semantic identifiers for authenticated top-level navigation.
 *
 * These values correspond to the application's TopLevelDestination enum:
 *
 * HOME
 * ADD_PROPERTY
 * CHATS
 * MARKET
 * PROFILE
 */
private object NavigationTestTags {

    const val HOME = "EstatiaNavItem_HOME"

    const val ADD_PROPERTY = "EstatiaNavItem_ADD_PROPERTY"

    const val CHATS = "EstatiaNavItem_CHATS"

    const val MARKET = "EstatiaNavItem_MARKET"

    const val PROFILE = "EstatiaNavItem_PROFILE"
}

/**
 * Stable identifiers for screens that represent important Baseline Profile
 * journeys.
 */
private object ScreenTestTags {

    const val PROPERTY_FEED = "PropertyFeed"

    const val SEARCH_FIELD = "SearchTextField"

    const val TOP_APP_BAR_NAVIGATION = "EstatiaTopAppBarNavIcon"

    const val ADD_PROPERTY = "AddPropertyScreen"

    const val CHATS = "ChatsScreen"

    const val MARKET = "MarketScreen"

    const val PROFILE = "ProfileScreen"
}

/**
 * Stable identifiers for the actual tabs exposed by ProfileScreen.
 */
private object ProfileTestTags {

    const val LISTINGS = "ProfileTab_LISTINGS"

    const val FAVORITES = "ProfileTab_FAVORITES"

    const val REVIEWS = "ProfileTab_REVIEWS"
}