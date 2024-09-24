package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.DownloadData
import com.github.panpf.sketch.util.defaultFileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DownloadDataTest {

    @Test
    fun testBytes() {
        val byteArray = byteArrayOf(1, 2, 3, 4, 5)
        val bytes = DownloadData.Bytes(byteArray)
        assertContentEquals(expected = byteArray, actual = bytes.bytes)
    }

    @Test
    fun testCache() {
        val fileSystem = defaultFileSystem()
        val path = "/tmp/testfile".toPath()
        val cache = DownloadData.Cache(fileSystem, path)
        assertEquals(expected = fileSystem, actual = cache.fileSystem)
        assertEquals(expected = path, actual = cache.path)
    }
}