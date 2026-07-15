package com.estatia.realestate.apps.core.network.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException

sealed class FirestoreException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {


    data object PermissionDenied :
        FirestoreException(
            "Firestore permission denied"
        )


    data object NotFound :
        FirestoreException(
            "Firestore document not found"
        )


    data object AlreadyExists :
        FirestoreException(
            "Firestore document already exists"
        )


    data object Aborted :
        FirestoreException(
            "Firestore operation aborted"
        )


    data object FailedPrecondition :
        FirestoreException(
            "Firestore failed precondition"
        )


    data object ResourceExhausted :
        FirestoreException(
            "Firestore resource exhausted"
        )


    data object Unavailable :
        FirestoreException(
            "Firestore service unavailable"
        )


    data object DeadlineExceeded :
        FirestoreException(
            "Firestore deadline exceeded"
        )


    data class Unknown(
        val original: Throwable
    ) : FirestoreException(
        "Unknown Firestore error",
        original
    )
}