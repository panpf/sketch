/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.svg.test.decode.toRequestContext
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

        val key1 = LoadRequest(context, newAssetUri("sample.svg")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.svg")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgBackgroundColor(Color.BLUE)
        }.toRequestContext().cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testSvgCss() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample.svg")) as ImageRequest).apply {
            Assert.assertNull(svgCss)
        }
        (DisplayRequest(context, newAssetUri("sample.svg")) {
            (this as Builder).svgCss("css1")
        } as ImageRequest).apply {
            Assert.assertEquals("css1", svgCss)
        }

        DisplayRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertNull(svgCss)
        }
        DisplayRequest(context, newAssetUri("sample.svg")) {
            svgCss("css1")
        }.apply {
            Assert.assertEquals("css1", svgCss)
        }

        LoadRequest(context, newAssetUri("sample.svg")).apply {
            Assert.assertNull(svgCss)
        }
        LoadRequest(context, newAssetUri("sample.svg")) {
            svgCss("css1")
        }.apply {
            Assert.assertEquals("css1", svgCss)
        }

        ImageOptions().apply {
            Assert.assertNull(svgCss)
        }
        ImageOptions {
            svgCss("css1")
        }.apply {
            Assert.assertEquals("css1", svgCss)
        }

        val key1 = LoadRequest(context, newAssetUri("sample.svg")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgCss("css1")
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = LoadRequest(context, newAssetUri("sample.svg")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample.svg")) {
            svgCss("css1")
        }.toRequestContext().cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }
}