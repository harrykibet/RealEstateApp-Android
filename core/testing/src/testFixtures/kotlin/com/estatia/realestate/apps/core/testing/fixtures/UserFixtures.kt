package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
import java.util.UUID

/**
 * Unified source of truth for user domain fixtures.
 */
object UserFixtures {

    /**
     * Returns a default tenant user model.
     */
    fun default(): UserDomainModel {
        return UserDomainModel(
            userId = "user_001",
            name = "Alice Wanjiku",
            email = "alice@example.com",
            phoneNumber = "+254712345678",
            profilePictureUrl = "https://randomuser.me/api/portraits/women/1.jpg",
            userType = UserType.TENANT,
            verificationLevel = VerificationLevel.IDENTITY_VERIFIED,
            likedProperties = listOf("property_101", "property_102")
        )
    }

    /**
     * Factory method for building customized or randomized user models.
     */
    fun build(
        id: String = UUID.randomUUID().toString(),
        name: String = "Generated User",
        type: UserType = UserType.TENANT
    ): UserDomainModel {
        return default().copy(
            userId = id,
            name = name,
            email = "$id@example.com",
            userType = type,
            verificationLevel = VerificationLevel.NONE,
            likedProperties = emptyList()
        )
    }

    val list = listOf(
        default(),
        UserDomainModel(
            userId = "user_002",
            name = "Brian Otieno",
            email = "brian@example.com",
            phoneNumber = "+254798765432",
            profilePictureUrl = "https://randomuser.me/api/portraits/men/2.jpg",
            userType = UserType.PROPERTY_OWNER,
            verificationLevel = VerificationLevel.NONE,
            likedProperties = listOf("property_103")
        ),
        UserDomainModel(
            userId = "user_003",
            name = "Clara Mwende",
            email = "clara@example.com",
            phoneNumber = "+254701234567",
            profilePictureUrl = "https://randomuser.me/api/portraits/women/3.jpg",
            userType = UserType.AGENT,
            verificationLevel = VerificationLevel.TRUSTED_PARTNER,
            likedProperties = emptyList()
        )
    )
}
