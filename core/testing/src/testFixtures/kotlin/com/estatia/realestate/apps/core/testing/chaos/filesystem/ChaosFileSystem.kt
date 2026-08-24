package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import java.io.File
import java.io.IOException

/**
 * Adversarial implementation of [IFileSystem] that can be scripted to fail.
 */
class ChaosFileSystem(private val delegate: IFileSystem) : IFileSystem by delegate {

    private var nextFailure: FileSystemFailure? = null

    /**
     * Script the next operation to fail.
     */
    fun failNext(failure: FileSystemFailure) {
        nextFailure = failure
    }

    override suspend fun writeBytes(file: File, bytes: ByteArray) {
        checkFailure()
        delegate.writeBytes(file, bytes)
    }

    override suspend fun readBytes(file: File): ByteArray {
        checkFailure()
        return delegate.readBytes(file)
    }

    private fun checkFailure() {
        when (val failure = nextFailure) {
            FileSystemFailure.DiskFull -> throw IOException("No space left on device (Chaos)")
            FileSystemFailure.PermissionDenied -> throw IOException("Permission denied (Chaos)")
            FileSystemFailure.MissingFile -> throw IOException("File not found (Chaos)")
            null -> Unit
        }
        nextFailure = null
    }

    sealed interface FileSystemFailure {
        data object DiskFull : FileSystemFailure
        data object PermissionDenied : FileSystemFailure
        data object MissingFile : FileSystemFailure
    }
}
