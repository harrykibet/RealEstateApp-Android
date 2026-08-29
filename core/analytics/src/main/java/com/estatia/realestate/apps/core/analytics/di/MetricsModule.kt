package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.analytics.MetricsRegistrar
import com.estatia.realestate.apps.core.analytics.MicrometerMetricsRegistrar
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MetricsModule {

    @Binds
    @Singleton
    abstract fun bindMetricsRegistrar(
        registrar: MicrometerMetricsRegistrar
    ): MetricsRegistrar

    companion object {
        @Provides
        @IntoSet
        fun providePrometheusMeterRegistry(): MeterRegistry =
            PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }
}
