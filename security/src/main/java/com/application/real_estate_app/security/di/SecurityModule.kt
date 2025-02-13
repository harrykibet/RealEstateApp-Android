package com.application.real_estate_app.security.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.application.real_estate_app.core.domain.interfaces.ISecurity
import com.application.real_estate_app.security.data.repositories.SecurityRepository
import com.application.real_estate_app.security.domain.interfaces.ISecurityRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    companion object {
        @Provides
        @Singleton
        fun provideEncryptedPreferences(context: Context): SharedPreferences {
            return EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

   @Binds
   @Singleton
    abstract fun bindSecurityRepo(securityRepository: SecurityRepository) : ISecurityRepo

    @Binds
    @Singleton
    abstract fun bindSecurity(securityRepository: SecurityRepository): ISecurity
}