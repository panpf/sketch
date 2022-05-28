package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.request.isDepthFromSaveCellularTraffic
import com.github.panpf.sketch.request.isIgnoredSaveCellularTraffic
import com.github.panpf.sketch.request.isSaveCellularTraffic
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.request.setDepthFromSaveCellularTraffic
import com.github.panpf.sketch.util.OtherException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveCellularTrafficExtensionsTest {

    @Test
    fun testSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).saveCellularTraffic(true)
        } as ImageRequest).apply {
            Assert.assertEquals(true, isSaveCellularTraffic)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            saveCellularTraffic(true)
        }.apply {
            Assert.assertEquals(true, isSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }
        ImageOptions {
            saveCellularTraffic(true)
        }.apply {
            Assert.assertEquals(true, isSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            saveCellularTraffic(true)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            saveCellularTraffic(true)
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnoreSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).ignoreSaveCellularTraffic(true)
        } as ImageRequest).apply {
            Assert.assertEquals(true, isIgnoredSaveCellularTraffic)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            ignoreSaveCellularTraffic(true)
        }.apply {
            Assert.assertEquals(true, isIgnoredSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }
        ImageOptions {
            ignoreSaveCellularTraffic(true)
        }.apply {
            Assert.assertEquals(true, isIgnoredSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignoreSaveCellularTraffic(true)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignoreSaveCellularTraffic(true)
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testDepthFromSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).setDepthFromSaveCellularTraffic()
        } as ImageRequest).apply {
            Assert.assertEquals(true, isDepthFromSaveCellularTraffic)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromSaveCellularTraffic()
        }.apply {
            Assert.assertEquals(true, isDepthFromSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }
        ImageOptions {
            setDepthFromSaveCellularTraffic()
        }.apply {
            Assert.assertEquals(true, isDepthFromSaveCellularTraffic)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromSaveCellularTraffic()
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromSaveCellularTraffic()
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIsCausedBySaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromSaveCellularTraffic()
        }

        Assert.assertFalse(OtherException(request, null, null).isCausedBySaveCellularTraffic)
        Assert.assertTrue(DepthException(request, LOCAL).isCausedBySaveCellularTraffic)
    }
}