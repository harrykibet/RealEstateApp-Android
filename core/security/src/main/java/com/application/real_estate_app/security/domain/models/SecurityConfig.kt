package com.application.real_estate_app.security.domain.models

data class SecurityConfig (
    val encryptionAlgorithm: String,
    val keyAlias: String
)