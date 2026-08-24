package com.estatia.realestate.apps.core.testing.fake.filesystem

import com.estatia.realestate.apps.core.common.interfaces.IFileSystem
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [IFileSystem] for fast, deterministic tests.
 */
class FakeFileSystem : IFileSystem {
    private val storage = ConcurrentHashMap<String, ByteArray>()

    override suspend fun exists(file: File): Boolean = storage.containsKey(file.path)

    override suspend fun readBytes(file: File): ByteArray {
        return storage[file.path] ?: throw java.io.FileNotFoundException(file.path)
    }

    override suspend fun writeBytes(file: File, bytes: ByteArray) {
        storage[file.path] = bytes
    }

    override suspend fun delete(file: File): Boolean {
        return storage.remove(file.path) != null
    }

    override suspend fun listFiles(directory: File): List<File>? {
        val path = directory.path
        return storage.keys().asSequence()
            .filter { it.startsWith(path) }
            .map { File(it) }
            .toList()
            .takeIf { it.isNotEmpty() }
    }
}
