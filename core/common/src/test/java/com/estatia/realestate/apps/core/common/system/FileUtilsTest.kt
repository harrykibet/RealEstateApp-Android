package com.estatia.realestate.apps.core.common.system

import com.estatia.realestate.apps.core.testing.chaos.filesystem.ChaosFileSystem
import com.estatia.realestate.apps.core.testing.chaos.filesystem.FileSystemBehavior
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.file.Files

class FileUtilsTest {

    private lateinit var fakeFs: FakeFileSystem
    private lateinit var chaosFs: ChaosFileSystem

    @Before
    fun setup() {
        fakeFs = FakeFileSystem()
        chaosFs = ChaosFileSystem(fakeFs)
    }

    @Test
    fun `doesFileExist returns true for existing file`() {
        val tempFile = Files.createTempFile("estatia_test", ".txt").toFile()
        try {
            assertTrue(FileUtils.doesFileExist(tempFile.absolutePath))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `doesFileExist returns false for non-existing file`() = runTest {
        // 🧪 Chaos Scenario: Missing File
        chaosFs.failNext(FileSystemBehavior.FileMissing)
        
        assertFalse(FileUtils.doesFileExist("/path/to/non/existing/file"))
    }

    @Test
    fun `deleteFile deletes existing file`() {
        val tempFile = Files.createTempFile("estatia_test_del", ".txt").toFile()
        assertTrue(tempFile.exists())
        
        val deleted = FileUtils.deleteFile(tempFile)
        
        assertTrue(deleted)
        assertFalse(tempFile.exists())
    }

    @Test
    fun `deleteFile handles null file safely`() = runTest {
        val result = FileUtils.deleteFile(null)
        assertFalse(result)
    }
}
