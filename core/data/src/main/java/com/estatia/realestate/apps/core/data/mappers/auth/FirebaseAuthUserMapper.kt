package com.estatia.realestate.apps.core.data.mappers.auth

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthUserMapper {

    fun fromFirebase(
        user: FirebaseUser
    ): AuthUserDomainModel {

        return AuthUserDomainModel(
            userId = user.uid,
            displayName = user.displayName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
            isEmailVerified = user.isEmailVerified
        )
    }
}
