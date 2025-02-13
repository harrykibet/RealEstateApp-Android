package com.application.real_estate_app.security.di

import com.application.real_estate_app.security.domain.interfaces.IApiKeyValidator
import com.application.real_estate_app.security.utils.extensions.ApiKeyValidator
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