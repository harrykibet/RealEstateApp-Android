package com.application.real_estate_app.core_database.di

import android.content.Context
import com.application.real_estate_app.core_database.PropertyDatabase
import com.application.real_estate_app.core_database.SearchDatabase
import com.application.real_estate_app.core_database.dao.PropertyDraftDao
import com.application.real_estate_app.core_database.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    // Provides the singleton instance of PropertyDatabase
    @Provides
    @Singleton
    fun providePropertyDatabase(@ApplicationContext context: Context): PropertyDatabase {
        return PropertyDatabase.getDatabase(context)
    }

    // Provides the PropertyDraftDao from the PropertyDatabase
    @Provides
    fun providePropertyDraftDao(propertyDatabase: PropertyDatabase): PropertyDraftDao {
        return propertyDatabase.propertyDraftDao()
    }

    @Provides
    @Singleton
    fun provideSearchDatabase(@ApplicationContext context: Context): SearchDatabase {
        return SearchDatabase.getDatabase(context)
    }

    @Provides
    fun provideSearchHistoryDao(searchDatabase: SearchDatabase): SearchHistoryDao {
        return searchDatabase.searchHistoryDao()
    }
}
