package com.estatia.realestate.apps.core.network.error_mappers.firebase

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import javax.inject.Inject


class FirebaseAuthErrorMapper @Inject constructor() : IAuthExceptionMapper {


    override fun map(
        throwable: Throwable
    ): AuthException {


        if (throwable !is FirebaseException) {
            return AuthException.Unknown(
                throwable
            )
        }


        return when (throwable) {


            is FirebaseAuthInvalidUserException ->
                AuthException.UserNotFound


            is FirebaseAuthInvalidCredentialsException ->
                AuthException.InvalidCredentials


            is FirebaseAuthUserCollisionException ->
                AuthException.UserAlreadyExists


            is FirebaseAuthRecentLoginRequiredException ->
                AuthException.SessionExpired


            is FirebaseAuthException -> {


                when (throwable.errorCode) {


                    "ERROR_WEAK_PASSWORD" ->
                        AuthException.Unknown(throwable) // Generic error if specific one missing


                    "ERROR_EMAIL_ALREADY_IN_USE" ->
                        AuthException.UserAlreadyExists


                    "ERROR_INVALID_EMAIL" ->
                        AuthException.InvalidEmail


                    "ERROR_USER_DISABLED" ->
                        AuthException.Unknown(throwable)


                    "ERROR_TOO_MANY_REQUESTS" ->
                        AuthException.TooManyRequests


                    "ERROR_OPERATION_NOT_ALLOWED" ->
                        AuthException.OperationNotAllowed


                    else ->
                        AuthException.Unknown(
                            throwable
                        )
                }
            }


            else ->
                AuthException.Unknown(
                    throwable
                )
        }
    }
}
