package com.estatia.realestate.apps.core.network.sources.firebase

import android.content.Context
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseAppCheckProxy
import com.google.firebase.FirebaseApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

/**
 * Firebase implementation of [IBackendInitializer].
 */
@Utility
internal class FirebaseBackendInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appCheckProxy: IFirebaseAppCheckProxy
) : IBackendInitializer {

    override suspend fun initialize() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
        
        appCheckProxy.initialize()
    }
}
