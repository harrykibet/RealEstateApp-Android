package com.application.real_estate_app.feature_property.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.application.real_estate_app.feature_property.data.dao.PropertyDraftDao
import com.application.real_estate_app.feature_property.data.entities.PropertyDraftEntity

@Database(entities = [PropertyDraftEntity::class], version = 1, exportSchema = false)
abstract class PropertyDatabase : RoomDatabase() {

    // Abstract function to get the PropertyDraftDao
    abstract fun propertyDraftDao(): PropertyDraftDao

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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
