package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
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
        val fs = ChaosFileSystem(FakeFileSystem())
        val file = File("test.txt")
        
        val job = async {
            delay(1.seconds)
            fs.writeBytes(file, byteArrayOf(1))
        }
        
        job.cancelAndJoin()
        assert(job.isCancelled)
    }

    @Test(expected = java.io.IOException::class)
    fun failNextDiskFullThrowsIOException() = runTest {
        val fs = createSubject(FileSystemBehavior.DiskFull)
        fs.writeBytes(File("test.txt"), byteArrayOf(1))
    }
}
