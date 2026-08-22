package com.estatia.realestate.apps.core.config.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AssetConfigDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun loadNetworkConfig(): String = loadFile("network_config.json")
    fun loadSecurityConfig(): String = loadFile("security_config.json")
    fun loadPlayerConfig(): String = loadFile("player_tuning_config.json")
    fun loadChaosConfig(): String = loadFile("chaos_config.json")

    private fun loadFile(fileName: String): String {
        return context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }
    }
}
