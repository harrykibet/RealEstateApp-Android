package com.estatia.realestate.apps.core.common.system

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFileSystem @Inject constructor() : IFileSystem {
    override suspend fun exists(file: File): Boolean = file.exists()

    override suspend fun readBytes(file: File): ByteArray = file.readBytes()

    override suspend fun writeBytes(file: File, bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    override suspend fun delete(file: File): Boolean = file.delete()

    override suspend fun listFiles(directory: File): List<File>? = directory.listFiles()?.toList()
}
