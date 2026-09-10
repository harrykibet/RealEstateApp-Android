package com.estatia.realestate.apps.core.localization.api

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Utility for reporting current timezone the device has set.
 * It always emits at least once with default setting and then for each TZ change.
 */
@Contract
interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}

/**
 * Global key used to obtain access to the current [TimeZone] through a CompositionLocal.
 */
val LocalTimeZone = staticCompositionLocalOf { TimeZone.currentSystemDefault() }
