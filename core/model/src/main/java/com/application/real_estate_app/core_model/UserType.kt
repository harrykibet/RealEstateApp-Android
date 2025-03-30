package com.application.real_estate_app.core_model

enum class UserType(val displayName: String) {
    TENANT("Tenant"),
    PROPERTY_OWNER("Property Owner");

    companion object {
        fun fromDisplayName(displayName: String): UserType? {
            return entries.find { it.displayName == displayName }
        }
    }
}
