package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.CommentException
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.common.exceptions.UserException
import com.estatia.realestate.apps.core.network.exceptions.FirestoreException
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject


class FirestoreErrorMapper @Inject constructor(
    userException: UserException,
    commentException: CommentException,
    propertyException: PropertyException
)
    : IFirestoreErrorMapper {


    override fun map(
        throwable: Throwable
    ): AppException {


        if (throwable !is FirebaseFirestoreException) {
            return userException.Unknown(
                throwable
            )
        }


        return when (throwable.code) {


            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                FirestoreException.PermissionDenied


            FirebaseFirestoreException.Code.NOT_FOUND ->
                FirestoreException.NotFound


            FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                FirestoreException.AlreadyExists


            FirebaseFirestoreException.Code.ABORTED ->
                FirestoreException.Aborted


            FirebaseFirestoreException.Code.FAILED_PRECONDITION ->
                FirestoreException.FailedPrecondition


            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
                FirestoreException.ResourceExhausted


            FirebaseFirestoreException.Code.UNAVAILABLE ->
                FirestoreException.Unavailable


            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                FirestoreException.DeadlineExceeded


            else ->
                FirestoreException.Unknown(
                    throwable
                )
        }
    }
}