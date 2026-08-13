package com.estatia.realestate.apps.core.data.mappers.firestore

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel

internal object FirestoreUserProfileMapper {

    // Map to Domain Model
    fun toDomain(user: UserEntityModel) : UserDomainModel {
        return UserDomainModel(
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl,
            bio = user.bio,
            userType = try { UserType.valueOf(user.userType) } catch (_: Exception) { UserType.TENANT },
            verificationLevel = try { VerificationLevel.valueOf(user.verificationLevel) } catch (_: Exception) { VerificationLevel.NONE },
            likedProperties = user.likedProperties,
            propertyCount = user.propertyCount,
            followerCount = user.followerCount,
            followingCount = user.followingCount
        )
    }

    fun toEntity(user: UserDomainModel): UserEntityModel {
        return UserEntityModel(
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl,
            bio = user.bio,
            userType = user.userType.name,
            verificationLevel = user.verificationLevel.name,
            likedProperties = user.likedProperties,
            propertyCount = user.propertyCount,
            followerCount = user.followerCount,
            followingCount = user.followingCount
        )
    }
}
