package com.estatia.realestate.apps.core.data.repositories

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.data.mappers.auth.NetworkUserMapper
import com.estatia.realestate.apps.core.data.mappers.remote.RemoteUserProfileMapper
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Domain-facing repository for all authentication and user identity operations.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Orchestrate identity flows between high-level application and remote identity providers (AWS/Firebase).
 * - Security: Does not store passwords or raw tokens; delegates to secure remote providers.
 * - Observability: Tracks success/failure for sign-in and sign-up funnels.
 * - Concurrency: Stateless; thread-safe for concurrent calls.
 */
internal class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
    private val metricsTracker: IMetricsTracker
) : IAuthRepository {

    override suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserDomainModel,
    ): AppResult<Unit> {
        val firestoreUser = RemoteUserProfileMapper.toEntity(user)
        return remoteDataSource.createOrUpdateUserProfile(userId, firestoreUser)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUserDomainModel> {
        return try {
            remoteDataSource.signUpWithEmail(email, password)
                .map { networkUser ->
                    metricsTracker.incrementCounter("auth.signup.success")
                    NetworkUserMapper.fromEntity(networkUser)
                }
        } catch (e: AuthException) {
            AppResult.Error(e)
        } catch (e: CancellationException) {
            // 🏎️ Fidelity: Rethrow cancellation to respect coroutine contracts.
            throw e
        } catch (e: Exception) {
            AppResult.Error(AuthException.Unknown(e))
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUserDomainModel> {
        return try {
            remoteDataSource.signInWithEmail(email, password)
                .map { networkUser ->
                    metricsTracker.incrementCounter("auth.signin.success")
                    NetworkUserMapper.fromEntity(networkUser)
                }
        } catch (e: AuthException) {
            AppResult.Error(e)
        } catch (e: CancellationException) {
            // 🏎️ Fidelity: Rethrow cancellation to respect coroutine contracts.
            throw e
        } catch (e: Exception) {
            AppResult.Error(AuthException.Unknown(e))
        }
    }

    override suspend fun signOut(): AppResult<Unit> {
        return remoteDataSource.signOut().also {
            metricsTracker.incrementCounter("auth.signout")
        }
    }

    override fun getCurrentUser(): AuthUserDomainModel? {
        return remoteDataSource
            .getCurrentUser()
            ?.let { networkUser ->
                NetworkUserMapper.fromEntity(networkUser)
            }
    }

    override suspend fun signInWithGoogle(
        idToken: String,
    ): AppResult<AuthUserDomainModel> {
        return remoteDataSource
            .signInWithGoogle(idToken)
            .map { networkUser ->
                metricsTracker.incrementCounter("auth.signin.google.success")
                NetworkUserMapper.fromEntity(networkUser)
            }
    }

    override suspend fun sendPasswordResetEmail(
        email: String,
    ): AppResult<Unit> {
        return remoteDataSource.sendPasswordResetEmail(email)
    }

    override fun isUserAuthenticated(): Flow<Boolean> {
        return remoteDataSource.isUserAuthenticated()
    }

    override fun getCurrentUserId(): String? {
        return remoteDataSource.getCurrentUserId()
    }

    override fun getCurrentUserEmail(): String? {
        return remoteDataSource.getCurrentUserEmail()
    }

    override fun startPhoneNumberVerification(
        phoneNumber: String,
        verificationContext: Any,
    ): Flow<PhoneVerificationState> {
        val activity = verificationContext as? Activity ?: error("Android Activity required for phone verification.")
        return remoteDataSource.startPhoneNumberVerification(phoneNumber, activity)
    }

    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String,
    ): AppResult<Unit> {
        return remoteDataSource.verifyPhoneCode(verificationId, code)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        verificationContext: Any,
    ): AppResult<String> {
        val activity = verificationContext as? Activity ?: error("Android Activity required for phone verification.")
        return remoteDataSource.resendVerificationCode(phoneNumber, activity)
    }

    override suspend fun sendEmailVerification(): AppResult<Unit> {
        return remoteDataSource.sendEmailVerification()
    }

    override suspend fun isEmailVerified(): AppResult<Boolean> {
        return remoteDataSource.isEmailVerified()
    }
}
