package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.setDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.util.OtherException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingExtensionsTest {

    @Test
    fun testPauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).pauseLoadWhenScrolling(true)
        } as ImageRequest).apply {
            Assert.assertEquals(true, isPauseLoadWhenScrolling)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            pauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertEquals(true, isPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }
        ImageOptions {
            pauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertEquals(true, isPauseLoadWhenScrolling)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            pauseLoadWhenScrolling(true)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            pauseLoadWhenScrolling(true)
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnorePauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).ignorePauseLoadWhenScrolling(true)
        } as ImageRequest).apply {
            Assert.assertEquals(true, isIgnoredPauseLoadWhenScrolling)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            ignorePauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertEquals(true, isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }
        ImageOptions {
            ignorePauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertEquals(true, isIgnoredPauseLoadWhenScrolling)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignorePauseLoadWhenScrolling(true)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            ignorePauseLoadWhenScrolling(true)
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testDepthFromPauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).setDepthFromPauseLoadWhenScrolling()
        } as ImageRequest).apply {
            Assert.assertEquals(true, isDepthFromPauseLoadWhenScrolling)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromPauseLoadWhenScrolling()
        }.apply {
            Assert.assertEquals(true, isDepthFromPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }
        ImageOptions {
            setDepthFromPauseLoadWhenScrolling()
        }.apply {
            Assert.assertEquals(true, isDepthFromPauseLoadWhenScrolling)
        }

        val key1 = DisplayRequest(context, newAssetUri("sample.svg")).key
        val key2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromPauseLoadWhenScrolling()
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = DisplayRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromPauseLoadWhenScrolling()
        }.cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIsCausedByPauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val request = DisplayRequest(context, newAssetUri("sample.svg")) {
            setDepthFromPauseLoadWhenScrolling()
        }

        Assert.assertFalse(OtherException(request, null, null).isCausedByPauseLoadWhenScrolling)
        Assert.assertTrue(DepthException(request, MEMORY).isCausedByPauseLoadWhenScrolling)
    }
}