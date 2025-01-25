package com.application.real_estate_app.core.di

import android.content.Context
import android.net.ConnectivityManager
import com.application.real_estate_app.core.domain.interfaces.INetworkHandler
import com.application.real_estate_app.core.network.NetworkHandler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkHandler(networkHandler: NetworkHandler): INetworkHandler

    companion object {

        @Provides
        @Singleton
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }
}
