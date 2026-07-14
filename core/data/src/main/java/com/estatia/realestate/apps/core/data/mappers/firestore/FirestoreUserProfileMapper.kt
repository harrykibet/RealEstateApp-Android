package com.estatia.realestate.apps.core.data.mappers.firestore

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel

object FirestoreUserProfileMapper {

    // Map to Domain Model
    fun toDomain(user: UserEntityModel) : UserDomainModel {
        return UserDomainModel(
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl,
            userType = UserType.valueOf(user.userType), // Convert String to Enum
            verified = user.verified,
            likedProperties = user.likedProperties
        )
    }

    fun toEntity(user: UserDomainModel): UserEntityModel {
        return UserEntityModel(
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl,
            userType = user.userType.name, // Convert Enum to String
            verified = user.verified,
            likedProperties = user.likedProperties
        )
    }
}