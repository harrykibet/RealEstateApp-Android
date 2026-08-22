package com.estatia.realestate.apps.core.network.error_mappers.aws

import com.amplifyframework.auth.AuthException
import com.estatia.realestate.apps.core.common.exceptions.AuthException as DomainAuthException
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AwsAuthErrorMapper @Inject constructor() : IAuthExceptionMapper {

    override fun map(throwable: Throwable): DomainAuthException {
        if (throwable !is AuthException) {
            return DomainAuthException.Unknown(throwable)
        }

        val message = throwable.message?.lowercase() ?: ""
        
        return when {
            message.contains("user not found") -> DomainAuthException.UserNotFound
            message.contains("user already exists") || message.contains("usernameexists") -> DomainAuthException.UserAlreadyExists
            message.contains("invalid password") || message.contains("invalidparameter") -> DomainAuthException.InvalidCredentials
            message.contains("not authorized") || message.contains("notauthorized") -> DomainAuthException.InvalidCredentials
            message.contains("session expired") -> DomainAuthException.SessionExpired
            message.contains("too many requests") -> DomainAuthException.TooManyRequests
            message.contains("code mismatch") -> DomainAuthException.ActionCodeInvalid
            message.contains("user not confirmed") -> DomainAuthException.EmailVerificationRequired
            else -> DomainAuthException.Unknown(throwable)
        }
    }
}
