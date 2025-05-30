package com.estatia.realestate.apps.core.model.user

enum class UserType(val displayName: String) {
    TENANT("Tenant"),
    AGENT("Agent"),
    PROPERTY_OWNER("Property Owner");

    companion object {
        fun fromDisplayName(displayName: String): UserType? {
            return entries.find { it.displayName == displayName }
        }
    }
}
