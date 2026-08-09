package com.estatia.realestate.apps.core.network.sources.firebase

import android.content.Context
import com.estatia.realestate.apps.core.network.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseAppCheckProxy
import com.google.firebase.FirebaseApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Firebase implementation of [IBackendInitializer].
 */
class FirebaseBackendInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appCheckProxy: IFirebaseAppCheckProxy
) : IBackendInitializer {

    override fun initialize() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        
        appCheckProxy.initialize()
    }
}
