package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.RemoteServiceException
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseErrorMapper
import com.google.firebase.FirebaseException
import javax.inject.Inject

class FirebaseFallbackErrorMapper @Inject constructor()
    : IFirebaseErrorMapper {


    override fun map(
        throwable: FirebaseException
    ): AppException {


        return RemoteServiceException.FirebaseUnknown(
            throwable
        )
    }
}
