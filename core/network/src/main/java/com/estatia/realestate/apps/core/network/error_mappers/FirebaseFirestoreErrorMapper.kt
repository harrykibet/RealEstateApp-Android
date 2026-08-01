package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject

class FirebaseFirestoreErrorMapper @Inject constructor() : IFirestoreErrorMapper {


    override fun map(
        throwable: Throwable
    ): DatabaseException {

        if (throwable !is FirebaseFirestoreException) {
            return DatabaseException.Unknown(
                throwable
            )
        }


        return when(throwable.code) {

            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                DatabaseException.PermissionDenied


            FirebaseFirestoreException.Code.NOT_FOUND ->
                DatabaseException.NotFound


            FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                DatabaseException.AlreadyExists


            FirebaseFirestoreException.Code.ABORTED ->
                DatabaseException.TransactionFailed


            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
                DatabaseException.ResourceExhausted


            FirebaseFirestoreException.Code.UNAVAILABLE ->
                DatabaseException.Unavailable


            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                DatabaseException.Timeout


            else ->
                DatabaseException.Unknown(
                    throwable
                )
        }
    }
}
