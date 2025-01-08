package com.application.real_estate_app.feature_auth.di

import android.content.Context
import com.application.real_estate_app.feature_auth.data.apis.AuthApi
import com.application.real_estate_app.feature_auth.domain.interfaces.GoogleSignInUtil
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthApi
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
abstract class AuthApiModule {

    //Bind AuthApi to IAuthApi
    @Binds
    @Singleton
    abstract fun bindAuthApi(api: AuthApi) : IAuthApi

    // Provide  GoogleSignInUtil client
    companion object {

        @Provides
        @Singleton
        fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
            return GoogleSignInUtil.getGoogleSignInClient(context)
        }
    }
}