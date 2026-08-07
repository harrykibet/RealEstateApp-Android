package com.estatia.realestate.apps.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.estatia.realestate.apps.core.database.dao.SearchCacheDao
import com.estatia.realestate.apps.core.database.dao.SearchHistoryDao
import com.estatia.realestate.apps.core.database.entities.SearchCacheEntity
import com.estatia.realestate.apps.core.database.entities.SearchHistoryEntity

@Database(
    entities = [
        SearchHistoryEntity::class,
        SearchCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SearchDatabase : RoomDatabase() {

    // Abstract function to get the SearchHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun searchCacheDao(): SearchCacheDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: SearchDatabase? = null

        // This method ensures that the database is only created once
        fun getDatabase(context: Context): SearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "search_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
