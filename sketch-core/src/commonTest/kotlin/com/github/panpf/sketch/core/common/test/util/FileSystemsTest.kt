package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.createFile
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.deleteContents
import okio.FileSystem
import okio.IOException
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileSystemsDesktopTest {

    @Test
    fun testCreateFile() {
        val fileSystem = FakeFileSystem()
        val testFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("testFile.txt")
        fileSystem.delete(testFile)
        assertFalse(fileSystem.exists(testFile))

        fileSystem.createDirectories(testFile.parent!!)
        fileSystem.createFile(testFile, mustCreate = false)
        assertTrue(fileSystem.exists(testFile))

        fileSystem.createFile(testFile, mustCreate = false)
        assertTrue(fileSystem.exists(testFile))

        assertFailsWith(IOException::class) {
            fileSystem.createFile(testFile, mustCreate = true)
        }
    }

    @Test
    fun testDeleteContents() {
        val fileSystem: FileSystem = defaultFileSystem()
        val directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("testDeleteContentsDir")
        val subDirectory = directory.resolve("subDir")
        val file1 = directory.resolve("file1.txt")
        val file2 = subDirectory.resolve("file2.txt")

        // Create directory structure
        fileSystem.createDirectories(subDirectory)
        fileSystem.write(file1) { writeUtf8("content1") }
        fileSystem.write(file2) { writeUtf8("content2") }

        // Verify files exist
        assertTrue(fileSystem.exists(file1))
        assertTrue(fileSystem.exists(file2))
        assertTrue(fileSystem.exists(subDirectory))

        // Delete contents
        fileSystem.deleteContents(directory)

        // Verify files are deleted
        assertTrue(!fileSystem.exists(file1))
        assertTrue(!fileSystem.exists(file2))
        assertTrue(fileSystem.exists(directory))
    }
}