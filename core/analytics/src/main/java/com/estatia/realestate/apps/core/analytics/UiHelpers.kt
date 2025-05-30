package com.estatia.realestate.apps.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.analytics.AnalyticsHelper
import com.estatia.realestate.apps.core.analytics.NoOpAnalyticsHelper

/**
 * Global key used to obtain access to the AnalyticsHelper through a CompositionLocal.
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    // Provide a default AnalyticsHelper which does nothing. This is so that tests and previews
    // do not have to provide one. For real app builds provide a different implementation.
    NoOpAnalyticsHelper()
}
