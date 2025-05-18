package com.application.real_estate_app.core_datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates from using lists to maps for user data.
 */
internal object ListToMapMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic id lists
            followedTopicIds.clear()
            followedTopicIds.putAll(
                currentData.deprecatedFollowedTopicIdsList.associateWith { true },
            )
            deprecatedFollowedTopicIds.clear()

            // Migrate author ids
            followedAuthorIds.clear()
            followedAuthorIds.putAll(
                currentData.deprecatedFollowedAuthorIdsList.associateWith { true },
            )
            deprecatedFollowedAuthorIds.clear()

            // Migrate bookmarks
            bookmarkedNewsResourceIds.clear()
            bookmarkedNewsResourceIds.putAll(
                currentData.deprecatedBookmarkedNewsResourceIdsList.associateWith { true },
            )
            deprecatedBookmarkedNewsResourceIds.clear()

            // Mark migration as complete
            hasDoneListToMapMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneListToMapMigration
}
