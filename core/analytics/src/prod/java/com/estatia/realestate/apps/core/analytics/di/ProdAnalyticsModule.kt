package com.estatia.realestate.apps.core.analytics.di

import com.estatia.realestate.apps.core.analytics.FirebaseAnalyticsHelper
import com.estatia.realestate.apps.core.analytics.IAnalyticsHelper
import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.registry.otlp.OtlpConfig
import io.micrometer.registry.otlp.OtlpMeterRegistry
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProdAnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: FirebaseAnalyticsHelper): IAnalyticsHelper

    companion object {
        @Provides
        @IntoSet
        fun provideOtlpMeterRegistry(
            networkConfig: INetworkConfig
        ): MeterRegistry {
            val otlpConfig = object : OtlpConfig {
                override fun url(): String {
                    val base = networkConfig.baseUrl
                    return if (base.endsWith("/")) "${base}v1/metrics" else "$base/v1/metrics"
                }
                override fun get(key: String): String? = null
                override fun step(): java.time.Duration = java.time.Duration.ofMinutes(1)
            }
            return OtlpMeterRegistry(otlpConfig, Clock.SYSTEM)
        }
    }
}
