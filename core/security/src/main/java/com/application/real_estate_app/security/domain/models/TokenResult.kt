package com.application.real_estate_app.security.domain.models

data class TokenResult (
    val token: String,
    val expiresAt: Long
)