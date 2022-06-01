package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
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

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).saveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isSaveCellularTraffic)
        }

        ImageOptions {
            saveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isSaveCellularTraffic)
        }
        ImageOptions {
            saveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isSaveCellularTraffic)
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

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).ignoreSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
        }

        ImageOptions {
            ignoreSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isIgnoredSaveCellularTraffic)
        }
        ImageOptions {
            ignoreSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isIgnoredSaveCellularTraffic)
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
    fun testSetDepthFromSaveCellularTraffic() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).setDepthFromSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as ImageRequest.Builder).setDepthFromSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            setDepthFromSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            setDepthFromSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions().apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
        }

        ImageOptions {
            setDepthFromSaveCellularTraffic(true)
        }.apply {
            Assert.assertTrue(isDepthFromSaveCellularTraffic)
        }
        ImageOptions {
            setDepthFromSaveCellularTraffic(false)
        }.apply {
            Assert.assertFalse(isDepthFromSaveCellularTraffic)
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

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
            setDepthFromSaveCellularTraffic()
        }.let {
            DepthException(it, it.depth)
        }.apply {
            Assert.assertTrue(isCausedBySaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
            setDepthFromSaveCellularTraffic()
        }.let {
            OtherException(it, null)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(NETWORK)
            setDepthFromSaveCellularTraffic()
        }.let {
            DepthException(it, it.depth)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
        }.let {
            DepthException(it, it.depth)
        }.apply {
            Assert.assertFalse(isCausedBySaveCellularTraffic)
        }
    }
}