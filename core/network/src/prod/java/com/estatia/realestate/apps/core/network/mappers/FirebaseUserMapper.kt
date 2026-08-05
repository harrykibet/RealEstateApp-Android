package com.estatia.realestate.apps.core.network.mappers

import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.google.firebase.auth.FirebaseUser

object FirebaseUserMapper {
    fun toEntity(user: FirebaseUser): NetworkUserEntity {
        return NetworkUserEntity(
            userId = user.uid,
            displayName = user.displayName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
            isEmailVerified = user.isEmailVerified
        )
    }
}
