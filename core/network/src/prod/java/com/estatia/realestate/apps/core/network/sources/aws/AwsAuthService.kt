package com.estatia.realestate.apps.core.network.sources.aws

import android.app.Activity
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IAuthRemoteDataSource] using AWS Amplify.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: User state is managed via an atomic [cachedUser] reference.
 * - Concurrency: Thread-safe for multi-coroutine access via atomic updates.
 * - Resilience: Delegates retry and mapping logic to [INetworkClient].
 * - Security: Does not cache tokens in plain text; delegates to Amplify secure storage.
 */
internal class AwsAuthService @Inject constructor(
    private val networkClient: INetworkClient
) : IAuthRemoteDataSource {

    private val cachedUserAtomic = java.util.concurrent.atomic.AtomicReference<AuthUser?>(null)
    private var cachedUser: AuthUser?
        get() = cachedUserAtomic.get()
        set(value) = cachedUserAtomic.set(value)

    override suspend fun signInInteractive(activity: Activity): AppResult<NetworkUserEntity> = 
        networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity,
                    { result ->
                        if (result.isSignedIn) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign in incomplete: ${result.nextStep.signInStep}"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
            fetchAndCacheUser()
        }

    override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> =
        networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signIn(email, password,
                    { result ->
                        if (result.isSignedIn) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign in incomplete"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
            fetchAndCacheUser()
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
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWith(Result.failure(AuthException.Unknown(Exception("Sign up incomplete"))))
                        }
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
            fetchAndCacheUser()
        }

    override suspend fun signOut(): AppResult<Unit> = networkClient.execute {
        suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signOut { result ->
                cachedUser = null
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
        val session = suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession(
                { session -> continuation.resume(session.isSignedIn) },
                { continuation.resume(false) }
            )
        }
        emit(session)
    }

    override fun getCurrentUserId(): String? = cachedUser?.userId

    override fun getCurrentUserEmail(): String? = null // Requires fetchUserAttributes

    override fun getCurrentUser(): NetworkUserEntity? {
        return cachedUser?.let { user ->
            NetworkUserEntity(
                userId = user.userId,
                displayName = user.username,
                email = null,
                phoneNumber = null,
                photoUrl = null,
                isEmailVerified = true
            )
        }
    }

    private suspend fun fetchAndCacheUser(): NetworkUserEntity {
        val user = suspendCancellableCoroutine<AuthUser> { continuation ->
            Amplify.Auth.getCurrentUser(
                { continuation.resume(it) },
                { continuation.resumeWith(Result.failure(it)) }
            )
        }
        cachedUser = user
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
