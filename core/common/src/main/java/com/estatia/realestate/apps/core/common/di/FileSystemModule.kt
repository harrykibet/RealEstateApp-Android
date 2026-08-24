package com.estatia.realestate.apps.core.common.di

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import com.estatia.realestate.apps.core.common.system.AndroidFileSystem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileSystemModule {

    @Binds
    @Singleton
    abstract fun bindFileSystem(
        fileSystem: AndroidFileSystem
    ): IFileSystem
}
