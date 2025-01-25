package com.application.real_estate_app.core.common.events

@Suppress("UNUSED")
object EventType {
    // General App Events
    const val EVENT_APP_LAUNCH = "App launched"
    const val EVENT_APP_CRASH = "App crashed"
    const val EVENT_USER_LOGIN = "User logged in"
    const val EVENT_USER_LOGOUT = "User logged out"
    const val EVENT_SESSION_EXPIRED = "User session expired"
    const val EVENT_NETWORK_ERROR = "Network error encountered"
    const val EVENT_API_ERROR = "API error encountered"
    const val EVENT_USER_REGISTRATION = "New user registered"

    // Property Listing Events
    const val EVENT_PROPERTY_VIEWED = "Property viewed by user"
    const val EVENT_PROPERTY_LIKED = "User liked a property"
    const val EVENT_PROPERTY_DISLIKED = "User disliked a property"
    const val EVENT_PROPERTY_SHARED = "User shared a property"
    const val EVENT_PROPERTY_INQUIRY = "User inquired about a property"
    const val EVENT_PROPERTY_SAVED = "User saved a property"
    const val EVENT_PROPERTY_REPORTED = "User reported a property"

    // User Profile Events
    const val EVENT_PROFILE_VIEWED = "User viewed their profile"
    const val EVENT_PROFILE_UPDATED = "User updated their profile"
    const val EVENT_PASSWORD_CHANGED = "User changed their password"
    const val EVENT_EMAIL_CHANGED = "User changed their email"
    const val EVENT_PROFILE_DELETED = "User deleted their account"

    // Search Events
    const val EVENT_SEARCH_STARTED = "User started a search"
    const val EVENT_SEARCH_FILTER_APPLIED = "User applied search filters"
    const val EVENT_SEARCH_RESULT_CLICKED = "User clicked on a search result"
    const val EVENT_MAP_VIEWED = "User viewed properties on map"
    const val EVENT_MAP_FILTER_APPLIED = "User applied filters on map view"

    // Monetization Events
    const val EVENT_SERVICE_FEE_PAID = "User paid service fee"
    const val EVENT_PAYMENT_SUCCESS = "Payment completed successfully"
    const val EVENT_PAYMENT_FAILURE = "Payment failed"
    const val EVENT_PROPERTY_ADVERTISED = "User advertised a property"
    const val EVENT_PROPERTY_BOOKED = "User booked a property"

    // Error Events
    const val EVENT_ERROR_LOG = "General error occurred"
    const val EVENT_WARNING_LOG = "General warning occurred"
    const val EVENT_DEBUG_LOG = "Debug message"
    const val EVENT_EXCEPTION_CAUGHT = "Exception caught"
    const val EVENT_DATABASE_ERROR = "Database error"

    // Analytics Events
    const val EVENT_PUSH_NOTIFICATION_RECEIVED = "User received push notification"
    const val EVENT_PUSH_NOTIFICATION_OPENED = "User opened push notification"
    const val EVENT_PROMOTIONAL_OFFER_VIEWED = "User viewed promotional offer"
    const val EVENT_PROMOTIONAL_OFFER_APPLIED = "User applied promotional offer"

    // Property Listing Management Events (Admin)
    const val EVENT_PROPERTY_CREATED = "Owner created a new property listing"
    const val EVENT_PROPERTY_UPDATED = "Owner updated a property listing"
    const val EVENT_PROPERTY_DELETED = "Owner deleted a property listing"
    const val EVENT_PROPERTY_AD_EDITED = "Owner edited the advertisement for a property"
}
