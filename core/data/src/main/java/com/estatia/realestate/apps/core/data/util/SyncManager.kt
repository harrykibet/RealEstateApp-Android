package com.estatia.realestate.apps.core.data.util

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if synchronization is in progress
 */
internal interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()
}
