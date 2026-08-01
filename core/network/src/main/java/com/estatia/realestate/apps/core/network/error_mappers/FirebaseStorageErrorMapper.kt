package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseStorageErrorMapper
import com.google.firebase.storage.StorageException as FirebaseStorageException
import javax.inject.Inject


class FirebaseStorageErrorMapper @Inject constructor()
    : IFirebaseStorageErrorMapper {


    override fun map(
        throwable: Throwable
    ): StorageException {


        if (throwable !is FirebaseStorageException) {
            return StorageException.Unknown(
                throwable
            )
        }


        return when (throwable.errorCode) {


            FirebaseStorageException.ERROR_NOT_AUTHORIZED ->
                StorageException.PermissionDenied


            FirebaseStorageException.ERROR_OBJECT_NOT_FOUND ->
                StorageException.ObjectNotFound


            FirebaseStorageException.ERROR_BUCKET_NOT_FOUND ->
                StorageException.BucketNotFound


            FirebaseStorageException.ERROR_QUOTA_EXCEEDED ->
                StorageException.QuotaExceeded


            FirebaseStorageException.ERROR_NOT_AUTHENTICATED ->
                StorageException.Unauthenticated


            FirebaseStorageException.ERROR_RETRY_LIMIT_EXCEEDED ->
                StorageException.RetryLimitExceeded


            FirebaseStorageException.ERROR_CANCELED ->
                StorageException.Cancelled


            FirebaseStorageException.ERROR_UNKNOWN ->
                StorageException.Unknown(
                    throwable
                )


            else ->
                StorageException.Unknown(
                    throwable
                )
        }
    }
}
