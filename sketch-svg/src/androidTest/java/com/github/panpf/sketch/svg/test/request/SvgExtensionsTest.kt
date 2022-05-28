package com.github.panpf.sketch.svg.test.request

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageRequest.Builder
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.svgBackgroundColor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgExtensionsTest {

    @Test
    fun testSvgBackgroundColor() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).svgBackgroundColor(Color.BLUE)
        } as ImageRequest).apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        LoadRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        LoadRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        ImageOptions().apply {
            Assert.assertNull(svgBackgroundColor)
        }
        ImageOptions {
            svgBackgroundColor(Color.BLUE)
        }.apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        val key1 = LoadRequest(context, newAssetUri("sample.svg")).key
        val key2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.svg")).cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }
}