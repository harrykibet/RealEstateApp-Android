package com.estatia.realestate.apps.core.testing.generators

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
import java.util.UUID

/**
 * Utility for generating realistic user domain models for testing.
 */
object UserGenerator {

    fun generateUser(
        id: String = UUID.randomUUID().toString(),
        name: String = "Generated User",
        type: UserType = UserType.TENANT
    ): UserDomainModel {
        return UserDomainModel(
            userId = id,
            name = name,
            email = "$id@example.com",
            phoneNumber = "+254700000000",
            profilePictureUrl = null,
            bio = "Bio for $id",
            userType = type,
            verificationLevel = VerificationLevel.NONE,
            likedProperties = emptyList()
        )
    }
}
