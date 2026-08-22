package com.estatia.realestate.apps.core.common.system

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class FileUtilsTest {

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
    fun `doesFileExist returns false for non-existing file`() {
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
    fun `deleteFile returns false for null file`() {
        assertFalse(FileUtils.deleteFile(null))
    }

    @Test
    fun `deleteFile returns false for non-existing file`() {
        val nonExistingFile = File("/path/to/non/existing/file")
        assertFalse(FileUtils.deleteFile(nonExistingFile))
    }
}
