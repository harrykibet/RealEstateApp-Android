package com.application.real_estate_app.core_model.security

data class TokenResult (
    val token: String,
    val expiresAt: Long
)