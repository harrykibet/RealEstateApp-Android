package com.application.real_estate_app.feature_auth.di

import android.content.Context
import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.application.real_estate_app.feature_auth.domain.interfaces.GoogleSignInUtil
import com.application.real_estate_app.feature_auth.data.services.AuthCheckerImpl
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    // Binds the AuthChecker interface to its implementation
    @Binds
    abstract fun bindAuthChecker(authCheckerImpl: AuthCheckerImpl): AuthRepository

    // Provide FirebaseAuth instance
    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
            return GoogleSignInUtil.getGoogleSignInClient(context)
        }
    }
}
