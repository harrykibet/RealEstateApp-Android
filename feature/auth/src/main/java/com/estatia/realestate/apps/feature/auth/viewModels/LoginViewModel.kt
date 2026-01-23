package com.estatia.realestate.apps.feature.auth.viewModels

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.feature.auth.state.AuthState
import com.estatia.realestate.apps.feature.auth.state.AuthState.Authenticated
import com.estatia.realestate.apps.feature.auth.state.AuthState.EmailVerificationRequired
import com.estatia.realestate.apps.feature.auth.state.AuthState.PhoneVerificationRequired
import com.estatia.realestate.apps.feature.auth.state.AuthState.Unauthenticated
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Suppress("unused")
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val authRepository: IAuthRepository
) : ViewModel() {

    companion object Signing {const val RC_SIGN_IN = 9001}
    val requestCode = RC_SIGN_IN

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn

    private val _googleSignInResult = MutableLiveData<Result<FirebaseUser?>>()
    val googleSignInResult: LiveData<Result<FirebaseUser?>> = _googleSignInResult

    fun loginUser(
        email: String, password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>? =
        authRepository.signInWithEmail(email, password, onFailure)

    fun isUserLoggedIn(): Boolean = authRepository.getCurrentUser() != null

    fun determineAuthState(user: FirebaseUser?): AuthState {
        if (user == null) return Unauthenticated
        if (!user.phoneNumber.isNullOrEmpty() && !user.isEmailVerified)
            return EmailVerificationRequired
        if (user.phoneNumber.isNullOrEmpty())
            return PhoneVerificationRequired
        return Authenticated
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        authRepository.firebaseAuthWithGoogle(idToken)
            ?.addOnSuccessListener { result ->
                val user = result.user
                _authState.value = determineAuthState(user)
            }
            ?.addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Google sign-in failed")
            }
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
}
