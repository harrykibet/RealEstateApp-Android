package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel

object AuthFixtures {
    fun authenticatedUser(
        id: String = "user_123",
        email: String = "test@example.com",
        isEmailVerified: Boolean = true
    ) = AuthUserDomainModel(
        userId = id,
        email = email,
        displayName = "Test User",
        isEmailVerified = isEmailVerified,
        phoneNumber = "+254700000000",
        photoUrl = null
    )
}
