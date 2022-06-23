package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isCausedByPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.setDepthFromPauseLoadWhenScrolling
import com.github.panpf.sketch.util.UnknownException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingExtensionsTest {

    @Test
    fun testPauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).pauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions {
            pauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        ImageOptions {
            pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
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

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).ignorePauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions {
            ignorePauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageOptions {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
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
    fun testSetDepthFromPauseLoadWhenScrolling() {
        val context = InstrumentationRegistry.getInstrumentation().context

        DisplayRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).setDepthFromPauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isDepthFromPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            (this as Builder).setDepthFromPauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            setDepthFromPauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isDepthFromPauseLoadWhenScrolling)
        }
        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            setDepthFromPauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
        }

        ImageOptions {
            setDepthFromPauseLoadWhenScrolling(true)
        }.apply {
            Assert.assertTrue(isDepthFromPauseLoadWhenScrolling)
        }
        ImageOptions {
            setDepthFromPauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isDepthFromPauseLoadWhenScrolling)
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

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(MEMORY)
            setDepthFromPauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isCausedByPauseLoadWhenScrolling(this, DepthException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(MEMORY)
            setDepthFromPauseLoadWhenScrolling()
        }.apply {
            Assert.assertFalse(isCausedByPauseLoadWhenScrolling(this, UnknownException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(LOCAL)
            setDepthFromPauseLoadWhenScrolling()
        }.apply {
            Assert.assertFalse(isCausedByPauseLoadWhenScrolling(this, DepthException("")))
        }

        DisplayRequest(context, "http://sample.com/sample.jpeg") {
            depth(MEMORY)
        }.apply {
            Assert.assertFalse(isCausedByPauseLoadWhenScrolling(this, DepthException("")))
        }
    }
}