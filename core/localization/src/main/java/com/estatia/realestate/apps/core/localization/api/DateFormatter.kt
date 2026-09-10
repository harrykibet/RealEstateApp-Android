package com.estatia.realestate.apps.core.localization.api

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for localized date and time formatting.
 */
@Contract
interface DateFormatter {
    fun formatDate(date: LocalDate): String
    fun formatDateTime(dateTime: LocalDateTime): String
    fun formatShortDate(date: LocalDate): String
    fun formatRelativeTime(instant: Instant): String
}
