package com.application.real_estate_app.core_network.di

import com.application.real_estate_app.core_network.interfaces.IApiKeyValidator
import com.application.real_estate_app.core_network.utils.ApiKeyValidator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiKeyValidatorModule {
    @Binds
    @Singleton
    abstract fun bindApiKeyValidator(validator: ApiKeyValidator): IApiKeyValidator
}