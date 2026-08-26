package com.estatia.realestate.apps.core.testing_network.fake.source

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.io.IOException

/**
 * Scriptable fake for [IAuthRemoteDataSource] that implements all [AuthBehavior] members.
 */
class FakeAuthRemoteDataSource(
    private val concurrencyChaos: ConcurrencyChaosController = ConcurrencyChaosController(),
    private val lifecycleChaos: LifecycleChaosController = LifecycleChaosController()
) : IAuthRemoteDataSource {

    private val _isAuthenticated = MutableStateFlow(false)
    private var nextBehavior: AuthBehavior = AuthBehavior.Authenticated
    private var currentUser: NetworkUserEntity? = null

    fun setNextBehavior(behavior: AuthBehavior) {
        nextBehavior = behavior
    }

    override fun isUserAuthenticated(): Flow<Boolean> = _isAuthenticated.asStateFlow()

    override fun getCurrentUserId(): String? = currentUser?.userId

    override fun getCurrentUserEmail(): String? = currentUser?.email

    override fun getCurrentUser(): NetworkUserEntity? = currentUser

    override suspend fun signUpWithEmail(email: String, password: String): AppResult<NetworkUserEntity> {
        checkChaos("sign_up")
        return simulateAuthSuccess(email)
    }

    override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> {
        checkChaos("sign_in")
        return simulateAuthSuccess(email)
    }

    override suspend fun signInInteractive(activity: Activity): AppResult<NetworkUserEntity> {
        checkChaos("sign_in_interactive")
        return simulateAuthSuccess("interactive-user@test.com")
    }

    override suspend fun signInWithGoogle(idToken: String): AppResult<NetworkUserEntity> {
        checkChaos("sign_in_google")
        return simulateAuthSuccess("google-user@test.com")
    }

    override suspend fun createOrUpdateUserProfile(userId: String, user: UserEntityModel): AppResult<Unit> {
        checkChaos("create_profile")
        return AppResult.Success(Unit)
    }

    override fun startPhoneNumberVerification(phoneNumber: String, activity: Activity): Flow<PhoneVerificationState> {
        return flowOf(PhoneVerificationState.CodeSent("vid_123"))
    }

    override suspend fun verifyPhoneCode(verificationId: String, code: String): AppResult<Unit> {
        checkChaos("verify_code")
        return AppResult.Success(Unit)
    }

    override suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): AppResult<String> {
        checkChaos("resend_code")
        return AppResult.Success("vid_456")
    }

    override suspend fun sendEmailVerification(): AppResult<Unit> {
        checkChaos("send_email_ver")
        return AppResult.Success(Unit)
    }

    override suspend fun isEmailVerified(): AppResult<Boolean> {
        checkChaos("is_email_ver")
        return AppResult.Success(currentUser?.isEmailVerified ?: false)
    }

    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> {
        checkChaos("send_pass_reset")
        return AppResult.Success(Unit)
    }

    override suspend fun signOut(): AppResult<Unit> {
        checkChaos("sign_out")
        currentUser = null
        _isAuthenticated.value = false
        return AppResult.Success(Unit)
    }

    private fun simulateAuthSuccess(email: String): AppResult<NetworkUserEntity> {
        val user = NetworkUserEntity(
            userId = "user_123",
            displayName = "Test User",
            email = email,
            phoneNumber = null,
            photoUrl = null,
            isEmailVerified = true
        )
        currentUser = user
        _isAuthenticated.value = true
        return AppResult.Success(user)
    }

    private fun checkChaos(point: String) {
        lifecycleChaos.checkChaos()
        // Note: we can't call suspend concurrencyChaos.checkChaos(point) here 
        // as some methods are not suspend or we want to keep logic simple in the fake.
        
        val behavior = nextBehavior
        nextBehavior = AuthBehavior.Authenticated

        when (behavior) {
            AuthBehavior.LoggedOut -> throw AuthException.UserNotFound
            AuthBehavior.TokenExpired -> throw AuthException.SessionExpired
            AuthBehavior.TokenRevoked -> throw AuthException.InvalidCredentials
            AuthBehavior.RefreshFails -> throw IOException("Token refresh failed (Chaos)")
            AuthBehavior.RefreshTimeout -> throw IOException("Token refresh timed out (Chaos)")
            AuthBehavior.MultipleRefreshRequests -> throw IllegalStateException("Concurrent refresh detected (Chaos)")
            AuthBehavior.LogoutDuringRefresh -> throw AuthException.SessionExpired
            AuthBehavior.LogoutDuringRequest -> throw AuthException.SessionExpired
            AuthBehavior.AccountDisabled -> throw AuthException.UserNotFound
            AuthBehavior.PermissionsRevoked -> throw AuthException.Unauthorized
            AuthBehavior.ProcessDeathDuringAuth -> throw IOException("Process death during auth (Chaos)")
            AuthBehavior.NetworkLostDuringRefresh -> throw IOException("Network lost during refresh (Chaos)")
            AuthBehavior.SessionRestorationFailure -> throw AuthException.SessionExpired
            AuthBehavior.Authenticated -> Unit
        }
    }
}
