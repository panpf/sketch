package com.github.panpf.sketch.core.common.test.source

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.source.cacheFile
import com.github.panpf.sketch.source.cacheFileOrNull
import com.github.panpf.sketch.source.getFileOrNull
import com.github.panpf.sketch.source.openSourceOrNull
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeDataSource
import okio.IOException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
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
    fun testCacheFile() {
        val (context, sketch) = getTestContextAndSketch()
        val dataSource = ResourceImages.jpeg.toDataSource(context)
        dataSource.cacheFile(sketch).apply {
            assertTrue(actual = toString().contains("/${DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME}/"))
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
}