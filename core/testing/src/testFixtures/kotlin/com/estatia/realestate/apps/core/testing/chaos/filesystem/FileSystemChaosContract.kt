package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

/**
 * Specialized contract for File System operations.
 */
abstract class FileSystemChaosContract : ChaosContract<IFileSystem, FileSystemBehavior>() {

    override val successBehavior = FileSystemBehavior.Success
    override val failureBehavior = FileSystemBehavior.IoFailure

    @Test(expected = IOException::class)
    fun diskFullThrowsIOException() = runTest {
        val fs = createSubject(FileSystemBehavior.DiskFull)
        performOperation(fs)
    }

    @Test(expected = IOException::class)
    fun fileMissingThrowsIOException() = runTest {
        val fs = createSubject(FileSystemBehavior.FileMissing)
        performOperation(fs)
    }
}
