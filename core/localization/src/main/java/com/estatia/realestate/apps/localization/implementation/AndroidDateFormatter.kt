package com.estatia.realestate.apps.localization.implementation

import com.estatia.realestate.apps.localization.api.DateFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

class AndroidDateFormatter @Inject constructor() : DateFormatter {
    override fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.getDefault())
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
        return dateTime.toJavaLocalDateTime().format(formatter)
    }

    override fun formatShortDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatRelativeTime(instant: Instant): String {
        // Simple implementation for now
        return instant.toString()
    }
}
