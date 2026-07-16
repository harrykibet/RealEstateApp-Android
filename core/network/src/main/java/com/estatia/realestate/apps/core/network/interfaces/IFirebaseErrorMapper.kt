package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.google.firebase.FirebaseException

interface IFirebaseErrorMapper {

    fun map(
        throwable: FirebaseException
    ): AppException
}