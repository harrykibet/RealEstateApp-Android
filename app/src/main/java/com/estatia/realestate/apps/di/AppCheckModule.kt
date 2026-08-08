package com.estatia.realestate.apps.di

import com.estatia.realestate.apps.FirebaseAppCheckProxyImpl
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseAppCheckProxy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppCheckModule {
    @Binds
    @Singleton
    abstract fun bindAppCheckProxy(impl: FirebaseAppCheckProxyImpl): IFirebaseAppCheckProxy
}
