package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import java.io.File
import java.io.IOException

/**
 * Adversarial implementation of [IFileSystem] that can be scripted to fail.
 * Consistently applies chaos to all operations in the interface.
 */
class ChaosFileSystem(private val delegate: IFileSystem) : IFileSystem {

    private var nextBehavior: FileSystemBehavior? = null

    /**
     * Script the next operation to fail.
     */
    fun failNext(behavior: FileSystemBehavior) {
        nextBehavior = behavior
    }

    override suspend fun exists(file: File): Boolean {
        checkFailure()
        return delegate.exists(file)
    }

    override suspend fun writeBytes(file: File, bytes: ByteArray) {
        checkFailure()
        delegate.writeBytes(file, bytes)
    }

    override suspend fun readBytes(file: File): ByteArray {
        checkFailure()
        return delegate.readBytes(file)
    }

    override suspend fun delete(file: File): Boolean {
        checkFailure()
        return delegate.delete(file)
    }

    override suspend fun listFiles(directory: File): List<File>? {
        checkFailure()
        return delegate.listFiles(directory)
    }

    private fun checkFailure() {
        val behavior = nextBehavior
        nextBehavior = null
        when (behavior) {
            FileSystemBehavior.DiskFull -> throw IOException("No space left on device (Chaos)")
            FileSystemBehavior.PermissionDenied -> throw IOException("Permission denied (Chaos)")
            FileSystemBehavior.FileMissing -> throw IOException("File not found (Chaos)")
            FileSystemBehavior.FileDisappearsDuringOp -> throw IOException("File disappeared (Chaos)")
            FileSystemBehavior.CorruptFile -> throw IOException("Corrupt file data (Chaos)")
            FileSystemBehavior.ZeroByteFile -> throw IOException("Zero-byte file (Chaos)")
            FileSystemBehavior.UnsupportedFormat -> throw IOException("Unsupported media format (Chaos)")
            FileSystemBehavior.WrongMimeType -> throw IOException("Wrong MIME type (Chaos)")
            FileSystemBehavior.PartialFile -> throw IOException("Partial file data (Chaos)")
            FileSystemBehavior.FileChangesWhileReading -> throw IOException("File content changed while reading (Chaos)")
            FileSystemBehavior.VeryLargeFile -> throw IOException("File size exceeds buffer limits (Chaos)")
            FileSystemBehavior.IoFailure -> throw IOException("General I/O failure (Chaos)")
            FileSystemBehavior.Success, null -> Unit
        }
    }
}
