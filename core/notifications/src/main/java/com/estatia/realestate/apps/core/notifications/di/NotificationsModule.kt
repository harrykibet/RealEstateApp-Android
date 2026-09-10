package com.estatia.realestate.apps.core.notifications.di

import com.estatia.realestate.apps.core.notifications.Notifier
import com.estatia.realestate.apps.core.notifications.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Module
@InstallIn(SingletonComponent::class)
@Helper
internal abstract class NotificationsModule {
    @Binds
    abstract fun bindNotifier(
        notifier: SystemTrayNotifier,
    ): Notifier
}
