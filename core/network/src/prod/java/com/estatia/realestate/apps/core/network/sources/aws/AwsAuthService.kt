package com.estatia.realestate.apps.core.network.sources.aws

import android.app.Activity
import android.content.Context
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IAuthRemoteDataSource] using AWS Amplify.
 */
internal class AwsAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkClient: INetworkClient
) : IAuthRemoteDataSource {

    override suspend fun signInInteractive(activity: Activity): AppResult<NetworkUserEntity> = 
        networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signInWithSocialWebUI(AuthProvider.GOOGLE, activity,
                    { result ->
                        if (result.isSignInComplete) {
                            continuation.resume(fetchCurrentUserSync())
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign in incomplete: ${result.nextStep.signInStep}"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }

    override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> =
        networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signIn(email, password,
                    { result ->
                        if (result.isSignInComplete) {
                            continuation.resume(fetchCurrentUserSync())
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign in incomplete"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }

    override suspend fun signUpWithEmail(email: String, password: String): AppResult<NetworkUserEntity> =
        networkClient.execute {
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build()

            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signUp(email, password, options,
                    { result ->
                        if (result.isSignUpComplete) {
                            continuation.resume(fetchCurrentUserSync())
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign up incomplete"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }

    override suspend fun signOut(): AppResult<Unit> = networkClient.execute {
        suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signOut { result ->
                when (result) {
                    is AWSCognitoAuthSignOutResult.CompleteSignOut -> continuation.resume(Unit)
                    is AWSCognitoAuthSignOutResult.PartialSignOut -> continuation.resume(Unit)
                    is AWSCognitoAuthSignOutResult.FailedSignOut -> continuation.resumeWith(Result.failure(result.exception))
                }
            }
        }
    }

    override suspend fun isEmailVerified(): AppResult<Boolean> = networkClient.execute {
        suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchUserAttributes(
                { attributes ->
                    val verified = attributes.find { it.key == AuthUserAttributeKey.emailVerified() }?.value?.toBoolean() ?: false
                    continuation.resume(verified)
                },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override fun isUserAuthenticated(): Flow<Boolean> = flow {
        val session = suspendCancellableCoroutine<Boolean> { continuation ->
            Amplify.Auth.fetchAuthSession(
                { session -> continuation.resume(session.isSignedIn) },
                { continuation.resume(false) }
            )
        }
        emit(session)
    }

    override fun getCurrentUserId(): String? {
        val user = Amplify.Auth.currentUser
        return user?.userId
    }

    override fun getCurrentUserEmail(): String? = null // Requires fetchUserAttributes, not easily synchronous

    override fun getCurrentUser(): NetworkUserEntity? {
        val user = Amplify.Auth.currentUser ?: return null
        return NetworkUserEntity(
            userId = user.userId,
            displayName = user.username,
            email = null,
            phoneNumber = null,
            photoUrl = null,
            isEmailVerified = true
        )
    }

    private fun fetchCurrentUserSync(): NetworkUserEntity {
        val user = Amplify.Auth.currentUser ?: throw AuthException.UserNotAuthenticated
        return NetworkUserEntity(
            userId = user.userId,
            displayName = user.username,
            email = null,
            phoneNumber = null,
            photoUrl = null,
            isEmailVerified = true
        )
    }

    override suspend fun createOrUpdateUserProfile(userId: String, user: UserEntityModel): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun signInWithGoogle(idToken: String): AppResult<NetworkUserEntity> = AppResult.Error(AuthException.Unknown(Exception("Use signInInteractive for AWS Social Auth")))
    override fun startPhoneNumberVerification(phoneNumber: String, activity: Activity): Flow<PhoneVerificationState> = flowOf(PhoneVerificationState.Idle)
    override suspend fun verifyPhoneCode(verificationId: String, code: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): AppResult<String> = AppResult.Success("")
    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> = networkClient.execute {
        suspendCancellableCoroutine { continuation ->
            Amplify.Auth.resetPassword(email,
                { continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }
    override suspend fun sendEmailVerification(): AppResult<Unit> = AppResult.Success(Unit)
}
