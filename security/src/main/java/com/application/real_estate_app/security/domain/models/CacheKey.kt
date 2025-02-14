package com.application.real_estate_app.security.domain.models

import com.application.real_estate_app.security.utils.extensions.SemanticVersion

data class CacheKey(
    val secretId: SecretId,
    val version: SemanticVersion,
    val environment: String
)