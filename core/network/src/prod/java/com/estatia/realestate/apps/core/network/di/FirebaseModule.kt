package com.estatia.realestate.apps.core.network.di

import android.content.Context
import android.util.Log
import com.estatia.realestate.apps.core.network.error_mappers.FirebaseAuthErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.FirebaseFallbackErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.FirebaseFirestoreErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.FirebaseStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    private const val TAG = "FirebaseModule"

    private fun initializeFirebaseIfNeeded(context: Context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            try {
                FirebaseApp.initializeApp(context)
                Log.d(TAG, "Firebase initialized successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Firebase initialization failed.", e)
                throw e
            }
        } else {
            Log.d(TAG, "Firebase already initialized.")
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        initializeFirebaseIfNeeded(context)
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(@ApplicationContext context: Context): FirebaseStorage {
        initializeFirebaseIfNeeded(context)
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        initializeFirebaseIfNeeded(context)
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        initializeFirebaseIfNeeded(context)
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(@ApplicationContext context: Context): FirebaseCrashlytics {
        initializeFirebaseIfNeeded(context)
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(@ApplicationContext context: Context): FirebaseRemoteConfig {
        initializeFirebaseIfNeeded(context)
        return FirebaseRemoteConfig.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebasePerformance(@ApplicationContext context: Context): FirebasePerformance {
        initializeFirebaseIfNeeded(context)
        return FirebasePerformance.getInstance()
    }

    @Provides
    @Singleton
    fun provideProjectId(@ApplicationContext context: Context): String {
        initializeFirebaseIfNeeded(context)
        return FirebaseApp.getInstance().options.projectId
            ?: throw IllegalStateException("Firebase Project ID is missing.")
    }

    @Provides
    @Singleton
    internal fun provideFirebaseFirestoreErrorMapper() : IFirestoreErrorMapper {
        return FirebaseFirestoreErrorMapper()
    }

    @Provides
    @Singleton
    internal fun provideFirebaseAuthenticationErrorMapper() : IAuthExceptionMapper {
        return FirebaseAuthErrorMapper()
    }

    @Provides
    @Singleton
    internal fun provideFirebaseStorageErrorMapper() : IFirebaseStorageErrorMapper {
        return FirebaseStorageErrorMapper()
    }

    @Provides
    @Singleton
    internal fun provideFirebaseFallbackErrorMapper() : IFirebaseErrorMapper {
        return FirebaseFallbackErrorMapper()
    }
}
