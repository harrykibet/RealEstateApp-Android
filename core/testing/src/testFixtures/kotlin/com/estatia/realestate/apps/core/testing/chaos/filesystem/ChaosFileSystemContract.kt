package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import com.estatia.realestate.apps.core.testing.lifecycle.launchAndDestroy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import kotlin.time.Duration.Companion.seconds

/**
 * Contract test for [ChaosFileSystem] to ensure it correctly applies chaos behaviors.
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class ChaosFileSystemContract : FileSystemChaosContract() {

    override fun createSubject(behavior: FileSystemBehavior): ChaosFileSystem {
        return ChaosFileSystem(FakeFileSystem()).apply {
            failNext(behavior)
        }
    }

    override suspend fun performOperation(subject: IFileSystem): Any? {
        return subject.exists(File("test.txt"))
    }

    @Test
    override fun cancellationPropagates() = runTest {
        val scheduler = TestScheduler()
        val hangingFs = object : IFileSystem by FakeFileSystem() {
            override suspend fun exists(file: File): Boolean {
                scheduler.release("hanging")
                delay(10.seconds)
                return true
            }
        }
        val fs = ChaosFileSystem(hangingFs)
        
        launchAndDestroy(scheduler, "hanging") {
            performOperation(fs)
        }
    }

    @Test(expected = java.io.IOException::class)
    fun failNextDiskFullThrowsIOException() = runTest {
        val fs = createSubject(FileSystemBehavior.DiskFull)
        fs.writeBytes(File("test.txt"), byteArrayOf(1))
    }


    @Test(expected = java.io.IOException::class)
    fun failNextPermissionDeniedAffectsExists() = runTest {
        val fs = createSubject(FileSystemBehavior.PermissionDenied)
        fs.exists(File("any.txt"))
    }

    @Test(expected = java.io.IOException::class)
    fun failNextFileMissingAffectsDelete() = runTest {
        val fs = createSubject(FileSystemBehavior.FileMissing)
        fs.delete(File("any.txt"))
    }

    @Test(expected = java.io.IOException::class)
    fun failNextIoFailureAffectsListFiles() = runTest {
        val fs = createSubject(FileSystemBehavior.IoFailure)
        fs.listFiles(File("any_dir"))
    }
}
