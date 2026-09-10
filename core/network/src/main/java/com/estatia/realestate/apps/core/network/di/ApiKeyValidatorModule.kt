package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.interfaces.IApiKeyValidator
import com.estatia.realestate.apps.core.network.utils.ApiKeyValidator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@Module
@InstallIn(SingletonComponent::class)
@EntityModel
abstract class ApiKeyValidatorModule {
    @Binds
    @Singleton
    internal abstract fun bindApiKeyValidator(validator: ApiKeyValidator): IApiKeyValidator
}
