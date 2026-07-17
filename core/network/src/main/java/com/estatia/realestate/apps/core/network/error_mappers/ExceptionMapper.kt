package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException as FirebaseStorageException
import javax.inject.Inject

class ExceptionMapper @Inject constructor(
    private val networkMapper: INetworkErrorMapper,
    private val authMapper: IAuthExceptionMapper,
    private val databaseMapper: IFirestoreErrorMapper,
    private val storageMapper: IFirebaseStorageErrorMapper,
    private val fallbackFirebaseMapper: IFirebaseErrorMapper
) : IExceptionMapper {


    override fun map(
        throwable: Throwable
    ): AppException {

        return when (throwable) {
            is FirebaseAuthException -> authMapper.map(throwable)
            is FirebaseFirestoreException -> databaseMapper.map(throwable)
            is FirebaseStorageException -> storageMapper.map(throwable)
            is FirebaseException -> fallbackFirebaseMapper.map(throwable)
            else -> networkMapper.map(throwable)
        }
    }
}