package com.github.panpf.sketch.core.jscommon.test.util

import com.github.panpf.sketch.util.ThrowingFileSystem
import com.github.panpf.sketch.util.defaultFileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FileSystemsJsCommonTest {

    @Test
    fun testDefaultFileSystem() {
        assertEquals(
            expected = ThrowingFileSystem,
            actual = defaultFileSystem(),
        )
    }

    @Test
    fun testThrowingFileSystem() {
        val dir1 = "/a/b/c".toPath()
        val dir2 = "/a/b/d".toPath()
        val file1 = "/a/b/d/sample.jpg".toPath()
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.atomicMove(source = dir1, target = dir2)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.canonicalize(path = dir1)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.createDirectory(dir = dir1, mustCreate = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.createDirectory(dir = dir1, mustCreate = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.createSymlink(source = dir1, target = dir2)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.delete(path = dir1, mustExist = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.delete(path = dir1, mustExist = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.list(dir = dir1)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.listOrNull(dir = dir1)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.metadataOrNull(path = dir1)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.openReadOnly(file = file1)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.openReadWrite(file = file1, mustCreate = true, mustExist = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.openReadWrite(file = file1, mustCreate = true, mustExist = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.openReadWrite(file = file1, mustCreate = false, mustExist = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.openReadWrite(file = file1, mustCreate = false, mustExist = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.sink(file = file1, mustCreate = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.sink(file = file1, mustCreate = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.appendingSink(file = file1, mustExist = true)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.appendingSink(file = file1, mustExist = false)
        }
        assertFailsWith(UnsupportedOperationException::class) {
            ThrowingFileSystem.source(file = file1)
        }
    }
}