package com.application.real_estate_app.core.di


import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.application.real_estate_app.core.data_utils.media_players.ExoPlayerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideExoPlayerManager(@ApplicationContext context: Context): ExoPlayerManager {
        return ExoPlayerManager(context)
    }
}
