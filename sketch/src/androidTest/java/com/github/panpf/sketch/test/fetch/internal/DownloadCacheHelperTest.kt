package com.github.panpf.sketch.test.fetch.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.fetch.internal.DownloadCacheHelper
import com.github.panpf.sketch.fetch.internal.DownloadCacheKeys
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.test.utils.TestHttpStack
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadCacheHelperTest {

    @Test
    fun testDownloadCacheKeys() {
        val context = getTestContext()
        val request = DownloadRequest(context, "http://sample.com/sample.jpeg")
        DownloadCacheKeys(request).apply {
            Assert.assertEquals("http://sample.com/sample.jpeg", dataDiskCacheKey)
            Assert.assertEquals(
                "http://sample.com/sample.jpeg_contentType",
                contentTypeDiskCacheKey
            )
        }
    }

    @Test
    fun testReadWrite() {
        val (context, sketch) = getTestContextAndNewSketch {
            httpStack(TestHttpStack(it))
        }

        val helper = DownloadCacheHelper(
            sketch,
            DownloadRequest(context, TestHttpStack.testImages.first().uriString)
        )
        val disabledHelper = DownloadCacheHelper(sketch, helper.request.newRequest {
            downloadCachePolicy(DISABLED)
        })

        sketch.downloadDiskCache.clear()
        Assert.assertNull(helper.read())

        runBlocking {
            disabledHelper.write(
                sketch.httpStack.getResponse(helper.request, helper.request.uriString),
                this
            )
        }.apply {
            Assert.assertNull(this)
        }
        Assert.assertNull(helper.read())

        runBlocking {
            helper.write(
                sketch.httpStack.getResponse(helper.request, helper.request.uriString),
                this
            )
        }.apply {
            Assert.assertNotNull(this)
        }
        helper.read().apply {
            Assert.assertNotNull(this)
            Assert.assertNotNull(this?.mimeType)
        }

        Assert.assertNull(disabledHelper.read())

        sketch.downloadDiskCache.remove(DownloadCacheKeys(helper.request).contentTypeDiskCacheKey)
        helper.read().apply {
            Assert.assertNotNull(this)
            Assert.assertNotNull(this?.mimeType)
        }

        sketch.downloadDiskCache.edit(DownloadCacheKeys(helper.request).contentTypeDiskCacheKey)!!.apply {
            newOutputStream().bufferedWriter().use {
                it.write("  ")
            }
            commit()
        }
        helper.read().apply {
            Assert.assertNotNull(this)
            Assert.assertNotNull(this?.mimeType)
        }
    }
}