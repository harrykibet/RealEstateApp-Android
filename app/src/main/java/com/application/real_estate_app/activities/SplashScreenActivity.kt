package com.application.real_estate_app.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.application.real_estate_app.feature_auth.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only show splash screen logic if running on API 30 or below
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            checkAuthentication()
        } else {
            // In case this is mistakenly launched on Android 12+, redirect to MainActivity
            redirectToMainActivity(isAuthenticated = false)
        }
    }

    private fun checkAuthentication() {
        // Trigger the asynchronous authentication check
        authViewModel.checkAuthentication()

        // Observe the authentication status
        authViewModel.isUserLoggedIn.observe(this) { isAuthenticated ->
            redirectToMainActivity(isAuthenticated)
        }
    }

    private fun redirectToMainActivity(isAuthenticated: Boolean) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_AUTHENTICATED", isAuthenticated)
        }
        startActivity(mainIntent)
        finish() // Close SplashScreenActivity
    }
}
