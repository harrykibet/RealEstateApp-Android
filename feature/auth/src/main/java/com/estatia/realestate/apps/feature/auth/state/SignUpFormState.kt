package com.estatia.realestate.apps.feature.auth.state

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiState

@Serializable
@UiState
data class SignUpFormState(
    val userName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val userType: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
