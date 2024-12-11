package com.application.real_estate_app.feature_profile.viewmodels

import androidx.lifecycle.ViewModel
import com.application.real_estate_app.domain.interfaces.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Suppress("unused") private val userRepository: IUserRepository
) : ViewModel() {
    // Your ViewModel logic here
}