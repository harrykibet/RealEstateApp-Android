package com.estatia.realestate.apps.core.intelligence.di

import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.intelligence.IVerificationService
import com.estatia.realestate.apps.core.domain.common.IContentSafetyService
import com.estatia.realestate.apps.core.intelligence.MlKitMediaIntelligenceService
import com.estatia.realestate.apps.core.intelligence.MlKitVerificationService
import com.estatia.realestate.apps.core.intelligence.MlKitContentSafetyService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IntelligenceModule {

    @Binds
    @Singleton
    abstract fun bindVerificationService(
        mlKitVerificationService: MlKitVerificationService
    ): IVerificationService

    @Binds
    @Singleton
    abstract fun bindMediaIntelligenceService(
        mlKitMediaIntelligenceService: MlKitMediaIntelligenceService
    ): IMediaIntelligenceService

    @Binds
    @Singleton
    abstract fun bindContentSafetyService(
        mlKitContentSafetyService: MlKitContentSafetyService
    ): IContentSafetyService
}
