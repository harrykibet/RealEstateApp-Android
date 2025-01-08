package com.application.real_estate_app.feature_search.di

import com.application.real_estate_app.feature_search.data.apis.SearchApi
import com.application.real_estate_app.feature_search.domain.interfaces.ISearchApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchApiModule {

    // bind IFeatureSearchRepo to FeatureSearchRepo
    @Binds
    @Singleton
    abstract fun bindSearchApi(api: SearchApi) : ISearchApi
}