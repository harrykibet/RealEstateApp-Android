package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import java.io.File
import java.io.IOException

/**
 * Adversarial implementation of [IFileSystem] that can be scripted to fail.
 */
class ChaosFileSystem(private val delegate: IFileSystem) : IFileSystem by delegate {

    private var nextBehavior: FileSystemBehavior? = null

    /**
     * Script the next operation to fail.
     */
    fun failNext(behavior: FileSystemBehavior) {
        nextBehavior = behavior
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
        when (nextBehavior) {
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
        nextBehavior = null
    }
}
