package com.estatia.realestate.apps.core.notifications.di

import com.estatia.realestate.apps.core.notifications.Notifier
import com.estatia.realestate.apps.core.notifications.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsModule {
    @Binds
    abstract fun bindNotifier(
        notifier: SystemTrayNotifier,
    ): Notifier
}
