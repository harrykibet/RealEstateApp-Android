package com.estatia.realestate.apps.core.network.error_mappers.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IInfrastructureErrorMapper
import com.google.firebase.FirebaseException
import javax.inject.Inject


class FirebaseFallbackErrorMapper @Inject constructor()
    : IInfrastructureErrorMapper {


    override fun map(
        throwable: Throwable
    ): AppException {

        if (throwable !is FirebaseException) {
            return NetworkException.Unknown(throwable)
        }

        return NetworkException.Unknown(
            throwable
        )
    }
}
