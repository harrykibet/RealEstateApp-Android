package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.analytics.FirebaseAnalyticsHelper
import com.estatia.realestate.apps.core.analytics.IAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProdAnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: FirebaseAnalyticsHelper): IAnalyticsHelper
}
