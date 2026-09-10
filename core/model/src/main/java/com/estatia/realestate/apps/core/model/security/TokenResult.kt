package com.estatia.realestate.apps.core.model.security
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class TokenResult (
    val token: String,
    val expiresAt: Long
)
