package com.estatia.realestate.apps.feature.profile.ui.viewmodels


import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.feature.profile.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            name = "Harry Kemboi",
            email = "truman948@gmail.com",
            bio = "Professional Real Estate Agent specializing in residential properties in Nairobi. Helping you find your dream home with ease.",
            userType = "Agent",
            stats = com.estatia.realestate.apps.feature.profile.ui.state.ProfileStats(
                propertyCount = 12,
                followerCount = 1200,
                followingCount = 450
            )
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}
