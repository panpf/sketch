package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.cacheFile
import com.github.panpf.sketch.source.cacheFileOrNull
import com.github.panpf.sketch.source.getFileOrNull
import com.github.panpf.sketch.source.openSourceOrNull
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import kotlinx.coroutines.test.runTest
import okio.IOException
import okio.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DataSourceTest {

    @Test
    fun testOpenSourceOrNull() {
        val fakeDataSource = FakeDataSource()
        assertFailsWith(IOException::class) {
            fakeDataSource.openSource()
        }
        assertNull(fakeDataSource.openSourceOrNull())
    }

    @Test
    fun testGetFileOrNull() {
        val (_, sketch) = getTestContextAndSketch()
        val fakeDataSource = FakeDataSource()
        assertFailsWith(IOException::class) {
            fakeDataSource.getFile(sketch)
        }
        assertNull(fakeDataSource.getFileOrNull(sketch))
    }

    @Test
    fun testCacheFile() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val dataSource = ComposeResImageFiles.jpeg.toDataSource(context)
        dataSource.cacheFile(sketch).apply {
            assertTrue(actual = toString().contains("${Path.DIRECTORY_SEPARATOR}${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}${Path.DIRECTORY_SEPARATOR}"))
        }
    }

    @Test
    fun testCacheFileOrNull() {
        val (_, sketch) = getTestContextAndSketch()
        val fakeDataSource = FakeDataSource()
        assertFailsWith(IOException::class) {
            fakeDataSource.cacheFile(sketch)
        }
        assertNull(fakeDataSource.cacheFileOrNull(sketch))
    }

    @Test
    fun testToByteArray() = runTest {
        val context = getTestContext()
        val jpeg = ComposeResImageFiles.jpeg
        val dataSource = jpeg.toDataSource(context)
        val byteArray = dataSource.toByteArray()
        assertEquals(jpeg.length, byteArray.size.toLong())

        val data = byteArrayOf(1, 2, 3)
        val dataSource2 = ByteArrayDataSource(data, DataFrom.LOCAL)
        assertSame(data, dataSource2.toByteArray())
    }
}