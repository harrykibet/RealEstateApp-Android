package com.estatia.realestate.apps.core.data.util

import kotlinx.coroutines.flow.Flow
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Reports on if synchronization is in progress
 */
@Contract
internal interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()
}
