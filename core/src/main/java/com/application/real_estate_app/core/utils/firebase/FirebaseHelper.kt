package com.application.real_estate_app.core.utils.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseHelper {

    fun getProjectIdFromFirebase(): String? {
        return try {
            val firebaseApp = FirebaseApp.getInstance()
            val options: FirebaseOptions = firebaseApp.options
            options.projectId
        } catch (e: Exception) {
            null
        }
    }
}
