package com.application.real_estate_app.feature_profile.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.application.real_estate_app.feature_profile.domain.interfaces.IProfileApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @Suppress("unused") private val api: IProfileApi
) : ViewModel() {
    // Your ViewModel logic here
}