package com.estatia.realestate.apps.core.model.security

import com.estatia.realestate.apps.core.model.utils.SemanticVersion

data class CacheKey(
    val secretId: SecretId,
    val version: SemanticVersion,
    val environment: String
)
