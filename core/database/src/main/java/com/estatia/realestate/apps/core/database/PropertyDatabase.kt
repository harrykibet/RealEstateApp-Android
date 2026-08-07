package com.estatia.realestate.apps.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.estatia.realestate.apps.core.database.dao.CommentCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.converters.RoomTypeConverters
import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity


@Database(
    entities = [
        PropertyCacheEntity::class,
        PropertyDraftEntity::class,
        CommentCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class PropertyDatabase : RoomDatabase() {

    // Abstract function to get the PropertyDraftDao
    abstract fun propertyDraftDao(): PropertyDraftDao
    abstract fun propertyCacheDao(): PropertyCacheDao
    abstract fun commentCacheDao(): CommentCacheDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        // This method ensures that the database is only created once
        fun getDatabase(context: Context): PropertyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertyDatabase::class.java,
                    "property_database"
                )
                    .fallbackToDestructiveMigration(false) // For demo purposes, we can reset on change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
