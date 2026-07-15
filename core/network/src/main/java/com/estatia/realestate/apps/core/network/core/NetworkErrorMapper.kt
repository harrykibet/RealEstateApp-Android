package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.domain.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class NetworkErrorMapper @Inject constructor()
    : INetworkErrorMapper {


    override fun map(
        throwable: Throwable
    ): NetworkException {


        return when(throwable) {


            is HttpException -> {

                when(throwable.code()) {


                    401 ->
                        NetworkException.Unauthorized


                    429 ->
                        NetworkException.RateLimited


                    in 500..599 ->
                        NetworkException.ServerError(
                            throwable.code()
                        )


                    else ->
                        NetworkException.ClientError(
                            throwable.code()
                        )
                }
            }


            is SocketTimeoutException ->
                NetworkException.Timeout


            is IOException ->
                NetworkException.ConnectionFailed

            is FirebaseAuthInvalidCredentialsException ->
                NetworkException.InvalidCredentials


            is FirebaseAuthUserCollisionException ->
                NetworkException.UserAlreadyExists


            is FirebaseAuthInvalidUserException ->
                NetworkException.UserNotFound


            is FirebaseTooManyRequestsException ->
                NetworkException.TooManyRequests

            is FirebaseAuthActionCodeException ->
                NetworkException.ActionCodeInvalid

            is FirebaseNetworkException ->
                NetworkException.NoInternet

            is FirebaseAuthEmailException ->
                NetworkException.InvalidEmail

            is FirebaseAuthMultiFactorException ->
                NetworkException.MultiFactorRequired

            else ->
                NetworkException.Unknown(
                    throwable
                )
        }
    }
}