package com.application.real_estate_app.feature_profile.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    // Your ViewModel logic here
    data class LogoutEvent(val message: String = "User logged out")
}