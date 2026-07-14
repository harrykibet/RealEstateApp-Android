package com.estatia.realestate.apps.core.data.mappers.auth

import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.google.firebase.auth.FirebaseUser

object AuthUserMapper {

    fun fromFirebase(
        user: FirebaseUser
    ): AuthUser {

        return AuthUser(
            userId = user.uid,
            displayName = user.displayName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
            isEmailVerified = user.isEmailVerified
        )
    }
}