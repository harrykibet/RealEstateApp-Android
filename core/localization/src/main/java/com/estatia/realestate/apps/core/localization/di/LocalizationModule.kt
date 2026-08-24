package com.estatia.realestate.apps.core.localization.di

import com.estatia.realestate.apps.core.localization.api.CurrencyFormatter
import com.estatia.realestate.apps.core.localization.api.DateFormatter
import com.estatia.realestate.apps.core.localization.api.LocaleProvider
import com.estatia.realestate.apps.core.localization.api.LocaleRepository
import com.estatia.realestate.apps.core.localization.api.MeasurementFormatter
import com.estatia.realestate.apps.core.localization.api.NumberFormatter
import com.estatia.realestate.apps.core.localization.api.PluralProvider
import com.estatia.realestate.apps.core.localization.api.StringProvider
import com.estatia.realestate.apps.core.localization.api.TimeZoneMonitor
import com.estatia.realestate.apps.core.localization.implementation.AndroidCurrencyFormatter
import com.estatia.realestate.apps.core.localization.implementation.AndroidDateFormatter
import com.estatia.realestate.apps.core.localization.implementation.AndroidLocaleProvider
import com.estatia.realestate.apps.core.localization.implementation.AndroidMeasurementFormatter
import com.estatia.realestate.apps.core.localization.implementation.AndroidNumberFormatter
import com.estatia.realestate.apps.core.localization.implementation.AndroidPluralProvider
import com.estatia.realestate.apps.core.localization.implementation.AndroidStringProvider
import com.estatia.realestate.apps.core.localization.implementation.AndroidTimeZoneMonitor
import com.estatia.realestate.apps.core.localization.implementation.LocaleRepositoryImpl
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
