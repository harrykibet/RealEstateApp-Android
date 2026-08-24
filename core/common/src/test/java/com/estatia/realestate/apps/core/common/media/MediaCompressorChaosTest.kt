package com.estatia.realestate.apps.core.common.media

import android.content.Context
import android.net.Uri
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.common.system.FileUtils
import com.estatia.realestate.apps.core.testing.chaos.filesystem.ChaosFileSystem
import com.estatia.realestate.apps.core.testing.fake.filesystem.FakeFileSystem
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File

class MediaCompressorChaosTest {

    private lateinit var logger: ILogger
    private lateinit var fakeFileSystem: FakeFileSystem
    private lateinit var chaosFileSystem: ChaosFileSystem
    private lateinit var compressor: MediaCompressor
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        logger = mockk(relaxed = true)
        fakeFileSystem = FakeFileSystem()
        chaosFileSystem = ChaosFileSystem(fakeFileSystem)
        compressor = MediaCompressor(logger, chaosFileSystem)
        
        mockkStatic(MediaFileUtils::class)
        mockkStatic(FileUtils::class)
    }

    @Test
    fun `compressImage returns null and logs error when disk is full`() = runTest {
        val uri = mockk<Uri>()
        val file = File("test.jpg")
        val outputDir = File("out")
        
        every { FileUtils.getFileFromUri(any(), any()) } returns file
        every { MediaFileUtils.getMediaFormat(any()) } returns MediaFormat.JPEG
        every { MediaFileUtils.isImage(any()) } returns true
        
        // 🧪 Chaos Injection: Simulate Disk Full
        chaosFileSystem.failNext(ChaosFileSystem.FileSystemFailure.DiskFull)

        val result = compressor.compressImage(context, uri, outputDir)

        assertNull(result)
        verify { logger.e(message = match { it.contains("compression failed") }, throwable = any()) }
    }
}
