package com.application.real_estate_app.core_model

import com.application.real_estate_app.core_model.utils.SemanticVersion

data class CacheKey(
    val secretId: SecretId,
    val version: SemanticVersion,
    val environment: String
)