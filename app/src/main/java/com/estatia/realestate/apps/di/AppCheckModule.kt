package com.estatia.realestate.apps.di

import com.estatia.realestate.apps.FirebaseAppCheckProxyImpl
import com.estatia.realestate.apps.core.network.interfaces.IFirebaseAppCheckProxy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Module
@InstallIn(SingletonComponent::class)
@Helper
internal abstract class AppCheckModule {
    @Binds
    @Singleton
    abstract fun bindAppCheckProxy(impl: FirebaseAppCheckProxyImpl): IFirebaseAppCheckProxy
}
