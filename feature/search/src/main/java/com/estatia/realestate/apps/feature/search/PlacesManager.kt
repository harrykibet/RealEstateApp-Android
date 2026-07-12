package com.estatia.realestate.apps.feature.search

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.estatia.realestate.apps.core.security.interfaces.ApiKeyProvider
import javax.inject.Inject

class PlacesManager @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider
) {

    fun initialize(context: Context) {
        Places.initialize(
            context,
            apiKeyProvider.mapsApiKey
        )
    }
}