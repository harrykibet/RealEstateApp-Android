package com.estatia.realestate.apps.core.data.mappers.auth

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity

object NetworkUserMapper {

    fun fromEntity(
        user: NetworkUserEntity
    ): AuthUserDomainModel {

        return AuthUserDomainModel(
            userId = user.userId,
            displayName = user.displayName,
            email = user.email,
            phoneNumber = user.phoneNumber,
            photoUrl = user.photoUrl,
            isEmailVerified = user.isEmailVerified
        )
    }
}
