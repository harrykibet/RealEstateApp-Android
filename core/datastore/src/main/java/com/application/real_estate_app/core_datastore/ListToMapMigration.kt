package com.application.real_estate_app.core_datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates from using lists to maps for user data.
 */
internal object ListToMapMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate property id lists
            followedPropertyIds.clear()
            followedPropertyIds.putAll(
                currentData.deprecatedFollowedPropertyIdsList.associateWith { true },
            )
            deprecatedFollowedPropertyIds.clear()

            // Migrate owner ids
            followedOwnerIds.clear()
            followedOwnerIds.putAll(
                currentData.deprecatedFollowedOwnerIdsList.associateWith { true },
            )
            deprecatedFollowedOwnerIds.clear()

            // Migrate bookmarks
            bookmarkedPropertyIds.clear()
            bookmarkedPropertyIds.putAll(
                currentData.deprecatedBookmarkedPropertyIdsList.associateWith { true },
            )
            deprecatedBookmarkedPropertyIds.clear()

            // Mark migration as complete
            hasDoneListToMapMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
