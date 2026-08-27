package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import java.util.UUID

/**
 * Unified source of truth for authentication domain fixtures.
 */
object AuthFixtures : FixtureContract<AuthUserDomainModel> {

    /**
     * Returns a rich, verified authenticated user model with deterministic values.
     */
    override fun default(): AuthUserDomainModel {
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
    override fun build(id: String): AuthUserDomainModel = build(id = id, email = "$id@example.com")

    /**
     * Overload for [build] to support custom attributes.
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

    override fun list(count: Int): List<AuthUserDomainModel> = List(count) { i ->
        if (i == 0) default() else build(id = "user_${123 + i}")
    }
}
