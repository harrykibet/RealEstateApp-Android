package com.estatia.realestate.apps.core.data

import com.estatia.realestate.apps.core.datastore.ChangeListVersions
import com.estatia.realestate.apps.core.model.utils.NetworkChangeList
import kotlin.coroutines.cancellation.CancellationException

/**
 * Global synchronization utilities for Estatia repositories.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the pull-based synchronization of local data with remote change lists.
 * - Concurrency: Serialization of sync tasks must be handled by the [Synchronizer] implementation.
 * - Resilience: Uses [suspendRunCatching] to safeguard against remote errors without breaking coroutine scopes.
 * - Lifecycle: Ensures cancellation propagates immediately to halt network I/O.
 */
/**
 * Global synchronization utilities for Estatia repositories.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the pull-based synchronization of local data with remote change lists.
 * - Concurrency: Serialization of sync tasks must be handled by the [Synchronizer] implementation.
 * - Resilience: Uses [suspendRunCatching] to safeguard against remote errors without breaking coroutine scopes.
 * - Lifecycle: Ensures cancellation propagates immediately to halt network I/O.
 */
interface Synchronizer {
    suspend fun getChangeListVersions(): ChangeListVersions

    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. 
 * Syncing MUST NOT be performed concurrently; it is the [Synchronizer]'s responsibility 
 * to ensure atomicity.
 */
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency.
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    // 🛡️ Cancellation must be propagated to maintain structured concurrency
    throw cancellationException
} catch (exception: Exception) {
    Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [versionReader] Reads the current version of the model that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
internal suspend fun Synchronizer.changeListSync(
    versionReader: (ChangeListVersions) -> Int,
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<String>) -> Unit,
) = suspendRunCatching {
    // Fetch the change list since last sync (akin to a git fetch)
    val currentVersion = versionReader(getChangeListVersions())
    val changeList = changeListFetcher(currentVersion)
    if (changeList.isEmpty()) return@suspendRunCatching true

    val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

    // Delete models that have been deleted server-side
    modelDeleter(deleted.map(NetworkChangeList::id))

    // Using the change list, pull down and save the changes (akin to a git pull)
    modelUpdater(updated.map(NetworkChangeList::id))

    // Update the last synced version (akin to updating local git HEAD)
    val latestVersion = changeList.last().changeListVersion
    updateChangeListVersions {
        versionUpdater(latestVersion)
    }
}.isSuccess
