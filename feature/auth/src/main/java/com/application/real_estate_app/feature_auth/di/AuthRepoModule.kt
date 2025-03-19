package com.application.real_estate_app.feature_auth.di

import android.content.Context
import com.application.real_estate_app.feature_auth.data.repositories.AuthRepository
import com.application.real_estate_app.feature_auth.domain.interfaces.GoogleSignInUtil
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthRepo
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepoModule {

    //Bind AuthApi to IAuthApi
    @Binds
    @Singleton
    abstract fun bindAuthApi(api: AuthRepository) : IAuthRepo

    // Provide  GoogleSignInUtil client
    companion object {

        @Provides
        @Singleton
        fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
            return GoogleSignInUtil.getGoogleSignInClient(context)
        }
    }
}