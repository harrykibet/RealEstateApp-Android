package com.application.real_estate_app.feature_property.di

import android.content.Context
import com.application.real_estate_app.feature_property.data.database.PropertyDatabase
import com.application.real_estate_app.feature_property.data.dao.PropertyDraftDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PropertyRoomModule {

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
}
