package com.application.real_estate_app.feature_auth.ui.viewModels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core_data.interfaces.IAuthRepository
import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_model.user.UserType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("unused")
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val authRepository: IAuthRepository
) : ViewModel() {

    companion object Signing {const val RC_SIGN_IN = 9001}
    val requestCode = RC_SIGN_IN

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    private var _isAuthCheckComplete = false
    fun isAuthCheckComplete() = _isAuthCheckComplete

    private val _phoneVerificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val phoneVerificationState: StateFlow<VerificationState> get() = _phoneVerificationState

    private val _resetPasswordStatus = MutableSharedFlow<Result<Boolean>>()
    val resetPasswordStatus: SharedFlow<Result<Boolean>> = _resetPasswordStatus

    private val _googleSignInResult = MutableLiveData<Result<FirebaseUser?>>()
    val googleSignInResult: LiveData<Result<FirebaseUser?>> = _googleSignInResult

    fun loginUser(email: String, password: String, onFailure: (Exception) -> Unit): Task<AuthResult>? =
        authRepository.signInWithEmail(email, password, onFailure)

    fun isUserLoggedIn(): Boolean = authRepository.getCurrentUser() != null

    fun checkAuthentication() {
        _isUserLoggedIn.postValue(isUserLoggedIn())
        _isAuthCheckComplete = true
    }


    fun isEmailVerified(): Boolean {
        return getCurrentUser()?.isEmailVerified ?: false
    }

    fun isPhoneVerified(): Boolean {
        return getCurrentUser()?.phoneNumber != null
    }

    private fun getCurrentUser() = authRepository.getCurrentUser()

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                authRepository.getCurrentUser()?.linkWithCredential(credential)?.await()
                _phoneVerificationState.value = VerificationState.Success
            } catch (e: Exception) {
                _phoneVerificationState.value =
                    VerificationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    suspend fun resetPassword(email: String, onFailure: (Exception) -> Unit){
        try {
            authRepository.sendPasswordResetEmail(email, onFailure)
            _resetPasswordStatus.emit(Result.success(true))
        } catch (e: Exception) {
            _resetPasswordStatus.emit(Result.failure(e))
        }
    }

    fun startPhoneNumberVerification(
        phoneNumber: String,
        onVerificationCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(authRepository.getFirebaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS) // 2 Minutes (120 seconds)
            .setCallbacks(onVerificationCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithGoogle(activity: Activity) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleGoogleSignInResult(data: Intent?, onFailure: (Exception) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                authRepository.firebaseAuthWithGoogle(it.idToken!!, onFailure)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _googleSignInResult.value = Result.success(task.result?.user)
                        } else {
                            _googleSignInResult.value = Result.failure(task.exception ?: Exception("Unknown Error"))
                        }
                    }
            }
        } catch (e: ApiException) {
            _googleSignInResult.value = Result.failure(e)
        }
    }


    fun registerUser(
        email: String,
        password: String,
        userName: String,
        phoneNumber: String,
        userType: UserType,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result: AuthResult? =
                    authRepository.signUpWithEmail(email, password, onFailure)?.await()
                val user = User(
                    userId = result?.user?.uid.orEmpty(),
                    name = userName,
                    email = email,
                    phoneNumber = phoneNumber,
                    profilePictureUrl = null,
                    userType = userType,
                    verified = false,
                    likedProperties = emptyList()
                )
                authRepository.createUserIfNotExists(user.userId, user, onFailure)
            } catch (e: Exception) {
                _phoneVerificationState.value =
                    VerificationState.Error("Sign-up failed: ${e.message}")
            }
        }
    }


    private fun sendEmailVerification() {
        viewModelScope.launch {
            try {
                authRepository.getCurrentUser()?.sendEmailVerification()?.await()
            } catch (e: Exception) {
                _phoneVerificationState.value =
                    VerificationState.Error("Verification email failed: ${e.message}")
            }
        }
    }

    // Verification states using sealed class
    sealed class VerificationState {
        data object Idle : VerificationState() // Default state
        data object Success : VerificationState()
        data class CodeSent(
            val verificationId: String,
            val token: PhoneAuthProvider.ForceResendingToken
        ) : VerificationState()

        data class Error(val message: String) : VerificationState()
    }
}
