package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.feature.auth.state.AuthState
import com.estatia.realestate.apps.feature.auth.state.AuthState.Authenticated
import com.estatia.realestate.apps.feature.auth.state.AuthState.EmailVerificationRequired
import com.estatia.realestate.apps.feature.auth.state.AuthState.PhoneVerificationRequired
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private fun determineAuthState(firebaseUser: FirebaseUser?): AuthState {
        if (firebaseUser == null) return AuthState.Unauthenticated

        if (!firebaseUser.isEmailVerified) {
            return EmailVerificationRequired(
                email = firebaseUser.email.orEmpty()
            )
        }

        if (firebaseUser.phoneNumber.isNullOrEmpty()) {
            return PhoneVerificationRequired(
                phoneNumber = firebaseUser.phoneNumber
            )
        }

        return Authenticated(
            user = firebaseUser.toDomainUser()
        )
    }

    private fun FirebaseUser.toDomainUser() = User(
        userId = uid,
        name = displayName,
        email = email,
        phoneNumber = phoneNumber,
        profilePictureUrl = photoUrl?.toString(),
        userType = UserType.TENANT, // default or fetched later
        verified = isEmailVerified,
        likedProperties = emptyList()
    )

    fun loginWithEmail(
        email: String,
        password: String
    ) {
        authRepository.signInWithEmail(email, password)
            ?.addOnSuccessListener { result ->
                _authState.value = determineAuthState(result.user)
            }
            ?.addOnFailureListener { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Login failed"
                )
            }
    }

    /**
     * 🔑 Called AFTER Google ID Token is obtained via Credential Manager
     */
    fun loginWithGoogleIdToken(idToken: String) {
        authRepository.firebaseAuthWithGoogle(idToken)
            ?.addOnSuccessListener { result ->
                _authState.value = determineAuthState(result.user)
            }
            ?.addOnFailureListener { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Google sign-in failed"
                )
            }
    }

    fun checkExistingSession() {
        _authState.value =
            determineAuthState(authRepository.getCurrentUser())
    }
}
