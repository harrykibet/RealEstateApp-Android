package com.estatia.realestate.apps.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.estatia.realestate.apps.core.database.dao.AnalyticsOutboxDao
import com.estatia.realestate.apps.core.database.entities.AnalyticsOutboxEntity

@Database(
    entities = [AnalyticsOutboxEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AnalyticsDatabase : RoomDatabase() {
    abstract fun analyticsOutboxDao(): AnalyticsOutboxDao

    companion object {
        @Volatile
        private var INSTANCE: AnalyticsDatabase? = null

        fun getDatabase(context: Context): AnalyticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnalyticsDatabase::class.java,
                    "analytics_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
