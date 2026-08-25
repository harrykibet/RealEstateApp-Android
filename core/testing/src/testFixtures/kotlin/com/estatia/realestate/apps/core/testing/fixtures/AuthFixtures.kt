package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import java.util.UUID

/**
 * Unified source of truth for authentication domain fixtures.
 */
object AuthFixtures {

    /**
     * Returns a rich, verified authenticated user model with deterministic values.
     */
    fun default(): AuthUserDomainModel {
        return AuthUserDomainModel(
            userId = "user_123",
            email = "test@example.com",
            displayName = "Test User",
            isEmailVerified = true,
            phoneNumber = "+254700000000",
            photoUrl = "https://example.com/photo.jpg"
        )
    }

    /**
     * Factory method for building customized or randomized authenticated user models.
     */
    fun build(
        id: String = UUID.randomUUID().toString(),
        email: String = "$id@example.com",
        isEmailVerified: Boolean = true
    ): AuthUserDomainModel {
        return default().copy(
            userId = id,
            email = email,
            isEmailVerified = isEmailVerified
        )
    }

    @Deprecated("Use default() or build()", ReplaceWith("default()"))
    fun authenticatedUser(
        id: String = "user_123",
        email: String = "test@example.com",
        isEmailVerified: Boolean = true
    ) = build(id, email, isEmailVerified)
}
