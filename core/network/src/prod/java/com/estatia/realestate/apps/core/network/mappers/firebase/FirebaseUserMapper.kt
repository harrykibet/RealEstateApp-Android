package com.estatia.realestate.apps.core.network.mappers.firebase

import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.google.firebase.auth.FirebaseUser


internal object FirebaseUserMapper {


    fun toEntity(
        firebaseUser: FirebaseUser
    ): NetworkUserEntity {

        return NetworkUserEntity(
            userId = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            phoneNumber = firebaseUser.phoneNumber,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }
}
