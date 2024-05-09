package com.github.panpf.sketch.core.nonjscommon.test.source

import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.getDataSourceCacheFile
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class DataSourceNonJsCommonTest {
    // TODO test

    @Test
    fun testGetCacheFileFromStreamDataSource() = runTest {
        val (context, sketch) = getTestContextAndNewSketch()
        ImageRequest(context, MyImages.jpeg.uri).fetch(sketch).dataSource.apply {
            val file = getDataSourceCacheFile(sketch, request, this)
            assertTrue(file.toString().contains("/sketch4/result/"))
            val file1 = getDataSourceCacheFile(sketch, request, this)
            assertEquals(file, file1)
        }

        assertFails {
            FileDataSource(
                sketch = sketch,
                request = ImageRequest(context, "https://fake.com/fake.jpeg"),
                path = "/sdcard/fake.jpeg".toPath()
            ).apply {
                getDataSourceCacheFile(sketch, request, this)
            }
        }
    }
}