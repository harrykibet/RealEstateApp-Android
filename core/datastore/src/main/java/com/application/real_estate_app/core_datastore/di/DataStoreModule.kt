package com.application.real_estate_app.core_datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import com.application.real_estate_app.core_network.utils.Dispatcher
import com.application.real_estate_app.core_network.utils.ReaDispatchers.IO
import com.application.real_estate_app.core_network.di.ApplicationScope
import com.application.real_estate_app.core_datastore.IntToStringIdsMigration
import com.application.real_estate_app.core_datastore.UserPreferencesSerializer

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            migrations = listOf(
                IntToStringIdsMigration,
            ),
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}
