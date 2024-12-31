package com.application.real_estate_app.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FireStoreConfig {
    fun initFireStoreSettings() {
        // No need to set persistence explicitly, FireStore automatically handles data persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}