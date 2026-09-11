package com.estatia.realestate.apps.core.common.interfaces

import java.io.File
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Abstraction for file system operations to ensure framework independence
 * and enable robust chaos testing.
 */
@Contract
interface IFileSystem {
    suspend fun exists(file: File): Boolean
    suspend fun readBytes(file: File): ByteArray
    suspend fun writeBytes(file: File, bytes: ByteArray)
    suspend fun delete(file: File): Boolean
    suspend fun listFiles(directory: File): List<File>?
}
