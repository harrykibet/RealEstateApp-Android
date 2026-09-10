package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.analytics.ObservabilityInitializer
import com.estatia.realestate.apps.core.analytics.MetricsTracker
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Module
@InstallIn(SingletonComponent::class)
@Helper
internal abstract class ObservabilityModule {

    @Binds
    @IntoSet
    abstract fun bindObservabilityInitializer(
        initializer: ObservabilityInitializer
    ): IBackendInitializer

    @Binds
    @Singleton
    abstract fun bindMetricsTracker(
        tracker: MetricsTracker
    ): IMetricsTracker
}
