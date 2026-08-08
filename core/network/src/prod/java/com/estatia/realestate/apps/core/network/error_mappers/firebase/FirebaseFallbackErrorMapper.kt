package com.estatia.realestate.apps.core.network.error_mappers.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseErrorMapper
import com.google.firebase.FirebaseException
import javax.inject.Inject


internal class FirebaseFallbackErrorMapper @Inject constructor()
    : IFirebaseErrorMapper {


    override fun map(
        throwable: FirebaseException
    ): AppException {

        return NetworkException.Unknown(
            throwable
        )
    }
}
