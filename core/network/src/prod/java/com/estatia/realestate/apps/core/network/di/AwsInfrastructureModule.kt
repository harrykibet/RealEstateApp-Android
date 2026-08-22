package com.estatia.realestate.apps.core.network.di

import com.estatia.realestate.apps.core.network.error_mappers.aws.AwsAuthErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.aws.AwsDatabaseErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.aws.AwsInfrastructureErrorMapper
import com.estatia.realestate.apps.core.network.error_mappers.aws.AwsStorageErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IDatabaseErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IInfrastructureErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.IStorageErrorMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AwsInfrastructureModule {

    @Provides
    @Singleton
    @AwsMapper
    fun provideAwsAuthErrorMapper(mapper: AwsAuthErrorMapper): IAuthExceptionMapper = mapper

    @Provides
    @Singleton
    @AwsMapper
    fun provideAwsDatabaseErrorMapper(mapper: AwsDatabaseErrorMapper): IDatabaseErrorMapper = mapper

    @Provides
    @Singleton
    @AwsMapper
    fun provideAwsStorageErrorMapper(mapper: AwsStorageErrorMapper): IStorageErrorMapper = mapper

    @Provides
    @Singleton
    @AwsMapper
    fun provideAwsInfrastructureErrorMapper(mapper: AwsInfrastructureErrorMapper): IInfrastructureErrorMapper = mapper
}
