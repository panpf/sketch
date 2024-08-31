package com.github.panpf.sketch.core.nonjscommon.test.source

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.cacheFile
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fetch
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class DataSourceNonJsCommonTest {

    @Test
    fun testCacheFile() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        ImageRequest(context, ResourceImages.jpeg.uri).fetch(sketch).dataSource.apply {
            val file = cacheFile(sketch)
            assertTrue(file.toString().contains("/sketch4/result/"))
            val file1 = cacheFile(sketch)
            assertEquals(file, file1)
        }

        assertFails {
            FileDataSource(path = "/sdcard/fake.jpeg".toPath()).apply {
                cacheFile(sketch)
            }
        }
    }
}