package com.estatia.realestate.apps.core.database.di

import android.content.Context
import com.estatia.realestate.apps.core.database.PropertyDatabase
import com.estatia.realestate.apps.core.database.SearchDatabase
import com.estatia.realestate.apps.core.database.dao.CommentCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyCacheDao
import com.estatia.realestate.apps.core.database.dao.PropertyDraftDao
import com.estatia.realestate.apps.core.database.dao.SearchCacheDao
import com.estatia.realestate.apps.core.database.dao.SearchHistoryDao
import com.estatia.realestate.apps.core.database.interfaces.IRoomExceptionMapper
import com.estatia.realestate.apps.core.database.mappers.RoomExceptionMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RoomModule {

    // Provides the singleton instance of PropertyDatabase
    @Provides
    @Singleton
    fun providePropertyDatabase(@ApplicationContext context: Context): PropertyDatabase {
        return PropertyDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSearchDatabase(@ApplicationContext context: Context): SearchDatabase {
        return SearchDatabase.getDatabase(context)
    }

    // Provides the PropertyDraftDao from the PropertyDatabase
    @Provides
    internal fun providePropertyDraftDao(propertyDatabase: PropertyDatabase): PropertyDraftDao {
        return propertyDatabase.propertyDraftDao()
    }

    @Provides
    internal fun providePropertyCacheDao(propertyDatabase: PropertyDatabase): PropertyCacheDao {
        return propertyDatabase.propertyCacheDao()
    }

    @Provides
    internal fun provideCommentCacheDao(propertyDatabase: PropertyDatabase): CommentCacheDao {
        return propertyDatabase.commentCacheDao()
    }

    @Provides
    internal fun provideSearchHistoryDao(searchDatabase: SearchDatabase): SearchHistoryDao {
        return searchDatabase.searchHistoryDao()
    }

    @Provides
    internal fun provideSearchCacheDao(searchDatabase: SearchDatabase): SearchCacheDao {
        return searchDatabase.searchCacheDao()
    }

    @Provides
    @Singleton
    internal fun provideRoomExceptionMapper(): IRoomExceptionMapper {
        return RoomExceptionMapper()
    }
}
