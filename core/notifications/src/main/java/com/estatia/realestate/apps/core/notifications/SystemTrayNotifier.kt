package com.estatia.realestate.apps.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.notifications.R.string
import com.estatia.realestate.apps.core.notifications.R.drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.estatia.realestate.apps.MainActivity"
private const val PROPERTIES_NOTIFICATION_REQUEST_CODE = 0
private const val PROPERTIES_NOTIFICATION_SUMMARY_ID = 1
private const val PROPERTIES_NOTIFICATION_CHANNEL_ID = ""
private const val PROPERTIES_NOTIFICATION_GROUP = "PROPERTIES_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.estatia.com"
private const val DEEP_LINK_HOME_PATH = "home"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_HOME_PATH"
const val DEEP_LINK_PROPERTIES_ID_KEY = "linkedPropertyId"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_PROPERTIES_ID_KEY}"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : Notifier {

    override fun postPropertiesNotifications(
        properties: List<Property>,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED) {

            val truncatedProperties = properties.take(MAX_NUM_NOTIFICATIONS)

            val propertiesNotifications = truncatedProperties.map { property ->
                createPropertiesNotification {
                    setSmallIcon(drawable.ic_estatia_notification)
                        .setContentTitle(property.title)
                        .setContentText(property.description)
                        .setContentIntent(propertiesPendingIntent(property))
                        .setGroup(PROPERTIES_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
            val summaryNotification = createPropertiesNotification {
                val title = getString(
                    string.core_notifications_properties_notification_group_summary,
                    truncatedProperties.size,
                )
                setContentTitle(title)
                    .setContentText(title)
                    .setSmallIcon(drawable.ic_estatia_notification)
                    // Build summary info into InboxStyle template.
                    .setStyle(propertiesNotificationStyle(truncatedProperties, title))
                    .setGroup(PROPERTIES_NOTIFICATION_GROUP)
                    .setGroupSummary(true)
                    .setAutoCancel(true)
                    .build()
            }

            // Send the notifications
            val notificationManager = NotificationManagerCompat.from(this)
            propertiesNotifications.forEachIndexed { index, notification ->
                notificationManager.notify(
                    truncatedProperties[index].id.hashCode(),
                    notification,
                )
            }
            notificationManager.notify(PROPERTIES_NOTIFICATION_SUMMARY_ID, summaryNotification)
        }
    }

    /**
     * Creates an inbox style summary notification for property updates
     */
    private fun propertiesNotificationStyle(
        properties: List<Property>,
        title: String,
    ): InboxStyle = properties
        .fold(InboxStyle()) { inboxStyle, property -> inboxStyle.addLine(property.title) }
        .setBigContentTitle(title)
        .setSummaryText(title)
}

/**
 * Creates a notification configured for property updates
 */
private fun Context.createPropertiesNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        PROPERTIES_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {

    val channel = NotificationChannel(
        PROPERTIES_NOTIFICATION_CHANNEL_ID,
        getString(string.core_notifications_properties_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(string.core_notifications_properties_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.propertiesPendingIntent(
    property: Property,
): PendingIntent? = PendingIntent.getActivity(
    this,
    PROPERTIES_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = property.propertiesDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun Property.propertiesDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()
