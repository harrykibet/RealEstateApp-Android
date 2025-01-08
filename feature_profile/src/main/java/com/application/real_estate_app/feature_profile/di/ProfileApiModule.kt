package com.application.real_estate_app.feature_profile.di

import com.application.real_estate_app.feature_profile.data.apis.ProfileApi
import com.application.real_estate_app.feature_profile.domain.interfaces.IProfileApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileApiModule {

    //Bind ProfileApi to IProfileApi interface
    @Binds
    @Singleton
    abstract fun bindProfileApi(api: ProfileApi) : IProfileApi
}