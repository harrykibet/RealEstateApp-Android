package com.estatia.realestate.apps.core.common.system

import android.content.Context
import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Standard implementation of [IFileSystem] using the JVM File API.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Low-level file I/O abstraction.
 * - Concurrency: Thread-safe (stateless).
 * - Resilience: Surfaces standard JVM I/O exceptions.
 * - Performance: Assumes calling context handles Dispatchers.IO.
 */
@Singleton
class AndroidFileSystem @Inject constructor(
    @ApplicationContext private val context: Context
) : IFileSystem {
    override suspend fun exists(file: File): Boolean = file.exists()

    override suspend fun readBytes(file: File): ByteArray = file.readBytes()

    override suspend fun writeBytes(file: File, bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    override suspend fun delete(file: File): Boolean = file.delete()

    override suspend fun listFiles(directory: File): List<File>? = directory.listFiles()?.toList()
}
