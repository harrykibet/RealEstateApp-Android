package com.estatia.realestate.apps.core.network.utils

/**
 * Registry of remote service identifiers used for secret resolution.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map domain service identifiers to platform-specific Secret Manager keys.
 * - Immutability: Pure value enumeration.
 */
enum class ServiceNames(val secretId: String) {
    PAYMENTS("StripePaymentAPIKey"),
    AUTH("Firebase API Key"),
    GOOGLE_PLAY_INTEGRITY("INTERGRITY_API_KEY"),
    AI_ML("AI_ML_API_Key");

    companion object {
        fun fromServiceName(service: String) =
            entries.firstOrNull { it.name.equals(service, ignoreCase = true) }
    }
}
