package com.application.real_estate_app.core_datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates saved ids from [Int] to [String] types
 */
internal object IntToStringIdsMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate property ids
            deprecatedFollowedPropertyIds.clear()
            deprecatedFollowedPropertyIds.addAll(
                currentData.deprecatedIntFollowedPropertyIdsList.map(Int::toString),
            )
            deprecatedIntFollowedPropertyIds.clear()

            // Migrate owner ids
            deprecatedFollowedOwnerIds.clear()
            deprecatedFollowedOwnerIds.addAll(
                currentData.deprecatedIntFollowedOwnerIdsList.map(Int::toString),
            )
            deprecatedIntFollowedOwnerIds.clear()

            // Mark migration as complete
            hasDoneIntToStringIdMigration = true
        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        !currentData.hasDoneIntToStringIdMigration
}
