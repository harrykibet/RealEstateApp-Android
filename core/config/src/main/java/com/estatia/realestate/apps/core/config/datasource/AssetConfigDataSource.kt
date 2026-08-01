package com.estatia.realestate.apps.core.config.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssetConfigDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun loadDefaultConfig(): String {
        return context.assets
            .open("remote_config_defaults.json")
            .bufferedReader()
            .use { it.readText() }
    }
}
