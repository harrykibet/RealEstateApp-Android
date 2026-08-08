package com.estatia.realestate.apps

import com.estatia.realestate.apps.core.network.interfaces.IFirebaseAppCheckProxy
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import javax.inject.Inject

internal class FirebaseAppCheckProxyImpl @Inject constructor() : IFirebaseAppCheckProxy {
    override fun initialize() {
        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
    }
}
