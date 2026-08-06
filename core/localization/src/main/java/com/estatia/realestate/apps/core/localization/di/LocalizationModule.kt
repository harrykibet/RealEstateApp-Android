package com.estatia.realestate.apps.core.localization.di

import com.estatia.realestate.apps.core.localization.api.*
import com.estatia.realestate.apps.core.localization.implementation.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalizationModule {

    @Binds
    @Singleton
    abstract fun bindStringProvider(impl: AndroidStringProvider): StringProvider

    @Binds
    @Singleton
    abstract fun bindLocaleProvider(impl: AndroidLocaleProvider): LocaleProvider

    @Binds
    @Singleton
    abstract fun bindLocaleRepository(impl: LocaleRepositoryImpl): LocaleRepository

    @Binds
    @Singleton
    abstract fun bindDateFormatter(impl: AndroidDateFormatter): DateFormatter

    @Binds
    @Singleton
    abstract fun bindNumberFormatter(impl: AndroidNumberFormatter): NumberFormatter

    @Binds
    @Singleton
    abstract fun bindCurrencyFormatter(impl: AndroidCurrencyFormatter): CurrencyFormatter

    @Binds
    @Singleton
    abstract fun bindMeasurementFormatter(impl: AndroidMeasurementFormatter): MeasurementFormatter

    @Binds
    @Singleton
    abstract fun bindPluralProvider(impl: AndroidPluralProvider): PluralProvider

    @Binds
    @Singleton
    abstract fun bindTimeZoneMonitor(impl: AndroidTimeZoneMonitor): TimeZoneMonitor
}
