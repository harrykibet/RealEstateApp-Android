package com.estatia.realestate.apps.core.model.security
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class SecurityConfig (
    val encryptionAlgorithm: String,
    val keyAlias: String
)
