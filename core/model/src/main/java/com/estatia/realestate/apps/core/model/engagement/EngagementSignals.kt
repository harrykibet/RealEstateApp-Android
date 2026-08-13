package com.estatia.realestate.apps.core.model.engagement

/**
 * Represents specific user actions that indicate engagement with a property listing.
 * These signals feed the server-side recommendation models.
 */
enum class EngagementAction {
    LIKE,
    SHARE,
    SAVE,
    FOLLOW,
    COMMENT_OPEN,
    SEARCH,
    VIEW
}
