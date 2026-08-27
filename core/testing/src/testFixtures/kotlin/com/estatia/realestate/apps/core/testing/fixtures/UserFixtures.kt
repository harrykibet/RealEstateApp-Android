package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
import java.util.UUID

/**
 * Unified source of truth for user domain fixtures.
 */
object UserFixtures : FixtureContract<UserDomainModel> {

    /**
     * Returns a default tenant user model.
     */
    override fun default(): UserDomainModel {
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
    override fun build(id: String): UserDomainModel = build(id = id, name = "Generated User")

    /**
     * Overload for [build] to support custom attributes.
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

    override fun list(count: Int): List<UserDomainModel> = List(count) { i ->
        if (i == 0) default() else build(id = "user_00${i + 1}")
    }
}
