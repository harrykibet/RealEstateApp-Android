package com.estatia.realestate.apps.core.localization.api

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Interface for localized date and time formatting.
 */
interface DateFormatter {
    fun formatDate(date: LocalDate): String
    fun formatDateTime(dateTime: LocalDateTime): String
    fun formatShortDate(date: LocalDate): String
    fun formatRelativeTime(instant: Instant): String
}
