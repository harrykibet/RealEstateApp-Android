package com.application.real_estate_app.core_model.security

@JvmInline
value class SecretId(val value: String) {
    init {
        require(value.matches(Regex("^[a-z0-9-]{5,50}$"))) {
            "Invalid Secret ID format"
        }
    }
}