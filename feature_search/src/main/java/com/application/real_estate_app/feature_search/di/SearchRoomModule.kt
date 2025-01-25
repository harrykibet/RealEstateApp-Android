package com.application.real_estate_app.feature_search.di

import android.content.Context
import com.application.real_estate_app.feature_search.data.sources.local.dao.SearchHistoryDao
import com.application.real_estate_app.feature_search.data.sources.local.database.SearchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchRoomModule {

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
