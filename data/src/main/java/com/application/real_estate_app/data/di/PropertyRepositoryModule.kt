package com.application.real_estate_app.data.di

import com.application.real_estate_app.data.repositories.PropertyRepositoryImpl
import com.application.real_estate_app.domain.interfaces.IPropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PropertyRepositoryModule {

    companion object {

        // Provide FirebaseFireStore instance
        @Provides
        @Singleton
        fun provideFirebaseFireStore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

        // Provide FirebaseStorage instance
        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }
    }

    // Bind IPropertyRepository to its implementation
    @Binds
    @Singleton
    abstract fun bindPropertyRepository(
        repository: PropertyRepositoryImpl
    ): IPropertyRepository
}
