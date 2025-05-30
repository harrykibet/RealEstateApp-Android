package com.estatia.realestate.apps.core.network.utils

// Enum class for API key types with actual Secret Manager key names
enum class ServiceNames(val secretId: String) {
    MAPS("MapsAPIKey"),
    PLACES("GooglePlacesAPIKey"),
    PAYMENTS("StripePaymentAPIKey"),
    AUTH("Firebase API Key"),
    GOOGLE_PLAY_INTEGRITY("INTERGRITY_API_KEY"),
    AI_ML("AI_ML_API_Key");

    companion object {
        fun fromServiceName(service: String) =
            entries.firstOrNull { it.name.equals(service, ignoreCase = true) }
    }
}
