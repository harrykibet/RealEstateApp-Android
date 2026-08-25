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
class ChaosFileSystemContract : ChaosContract<ChaosFileSystem, FileSystemBehavior>() {

    override fun createSubject(behavior: FileSystemBehavior): ChaosFileSystem {
        return ChaosFileSystem(FakeFileSystem()).apply {
            failNext(behavior)
        }
    }

    @Test
    override fun cancellationPropagates() = runTest {
        val scheduler = TestScheduler()
        val hangingFs = object : IFileSystem by FakeFileSystem() {
            override suspend fun writeBytes(file: File, bytes: ByteArray) {
                scheduler.release("writing")
                delay(10.seconds)
            }
        }
        val fs = ChaosFileSystem(hangingFs)
        
        launchAndDestroy(scheduler, "writing") {
            fs.writeBytes(File("test.txt"), byteArrayOf(1))
        }
    }

    @Test(expected = java.io.IOException::class)
    fun failNextDiskFullThrowsIOException() = runTest {
        val fs = createSubject(FileSystemBehavior.DiskFull)
        fs.writeBytes(File("test.txt"), byteArrayOf(1))
    }
}
