package com.application.real_estate_app.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.Provides
import javax.inject.Singleton
import javax.inject.Named
import dagger.hilt.components.SingletonComponent

// Create a URL holder class
class BaseUrlHolder {
    var url: String = "https://firestore-72e4c.firebaseapp.com/" //Default URL
        private set

    fun updateBaseUrl(newUrl: String) {
        url = newUrl
    }
}

// Update DI Module
@Module
@InstallIn(SingletonComponent::class)
object DynamicUrlModule {
    @Provides
    @Singleton
    fun provideBaseUrlHolder() = BaseUrlHolder()

    @Provides
    @Named("DynamicBaseUrl")
    fun provideDynamicBaseUrl(holder: BaseUrlHolder) = holder.url
}

// Usage to update URL
//val urlHolder: BaseUrlHolder by inject()
//urlHolder.updateBaseUrl("https://new-api.example.com/")