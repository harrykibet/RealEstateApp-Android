package com.estatia.realestate.apps.core.testing.chaos.filesystem

import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Concrete implementation of [ChaosFileSystemContract].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChaosFileSystemTest : ChaosFileSystemContract() {

    @Test
    fun `Success behavior works normally`() = runTest {
        val delegate = FakeFileSystem()
        val fs = ChaosFileSystem(delegate)
        val file = File("test.txt")
        val data = byteArrayOf(1, 2, 3)
        
        fs.writeBytes(file, data)
        assertTrue(fs.exists(file))
        assertTrue(fs.readBytes(file).contentEquals(data))
    }

    @Test
    fun `failNext only affects one operation`() = runTest {
        val fs = ChaosFileSystem(FakeFileSystem())
        fs.failNext(FileSystemBehavior.IoFailure)
        
        var exceptionThrown = false
        try {
            fs.exists(File("test.txt"))
        } catch (e: IOException) {
            exceptionThrown = true
        }
        
        assertTrue("Should have thrown IOException on first call", exceptionThrown)
        
        // Second call should succeed
        fs.exists(File("test.txt"))
    }

    @Test
    fun `failNext is atomic under heavy concurrency`() = runTest {
        val fs = ChaosFileSystem(FakeFileSystem())
        val iterations = 100
        val concurrency = 10
        val totalFailures = AtomicInteger(0)
        val totalSuccesses = AtomicInteger(0)
        
        repeat(iterations) {
            fs.failNext(FileSystemBehavior.IoFailure)
            val jobs = List(concurrency) {
                launch {
                    try {
                        fs.exists(File("test.txt"))
                        totalSuccesses.incrementAndGet()
                    } catch (e: IOException) {
                        totalFailures.incrementAndGet()
                    }
                }
            }
            jobs.joinAll()
        }
        
        assertEquals("Should have exactly one failure per failNext call", iterations, totalFailures.get())
        assertEquals("Total successful calls should be iterations * (concurrency - 1)", iterations * (concurrency - 1), totalSuccesses.get())
    }
}
