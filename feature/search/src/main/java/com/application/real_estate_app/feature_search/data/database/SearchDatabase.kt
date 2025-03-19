package com.application.real_estate_app.feature_search.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.real_estate_app.feature_search.data.dao.SearchHistoryDao
import com.application.real_estate_app.feature_search.data.entities.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {

    // Abstract function to get the SearchHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao

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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
