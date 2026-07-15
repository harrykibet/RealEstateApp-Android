package com.estatia.realestate.apps.core.network.error_mappers

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.FirebaseTooManyRequestsException
import javax.inject.Inject

class FirebaseAuthErrorMapper @Inject constructor() {


    fun map(
        throwable: Throwable
    ): AuthException {

        return when (throwable) {

            is FirebaseAuthInvalidCredentialsException ->
                AuthException.InvalidCredentials


            is FirebaseAuthUserCollisionException ->
                AuthException.UserAlreadyExists


            is FirebaseAuthInvalidUserException ->
                AuthException.UserNotFound


            is FirebaseTooManyRequestsException ->
                AuthException.TooManyRequests


            is FirebaseAuthActionCodeException ->
                AuthException.ActionCodeInvalid


            is FirebaseAuthEmailException ->
                AuthException.InvalidEmail


            is FirebaseAuthMultiFactorException ->
                AuthException.MultiFactorRequired


            else ->
                AuthException.Unknown(throwable)
        }
    }
}