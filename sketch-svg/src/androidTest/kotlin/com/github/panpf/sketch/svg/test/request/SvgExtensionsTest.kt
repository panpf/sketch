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
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgExtensionsTest {

    @Test
    fun testSvgBackgroundColor() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        ImageRequest(context, AssetImages.svg.uri) {
            this.svgBackgroundColor(Color.BLUE)
        }.apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        ImageRequest(context, AssetImages.svg.uri) {
            svgBackgroundColor(Color.BLUE)
        }.apply {
            Assert.assertEquals(Color.BLUE, svgBackgroundColor)
        }

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgBackgroundColor)
        }
        ImageRequest(context, AssetImages.svg.uri) {
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

        val key1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.svg.uri) {
            svgBackgroundColor(Color.BLUE)
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.svg.uri) {
            svgBackgroundColor(Color.BLUE)
        }.toRequestContext(sketch).cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testSvgCss() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgCss)
        }
        ImageRequest(context, AssetImages.svg.uri) {
            this.svgCss("css1")
        }.apply {
            Assert.assertEquals("css1", svgCss)
        }

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgCss)
        }
        ImageRequest(context, AssetImages.svg.uri) {
            svgCss("css1")
        }.apply {
            Assert.assertEquals("css1", svgCss)
        }

        ImageRequest(context, AssetImages.svg.uri).apply {
            Assert.assertNull(svgCss)
        }
        ImageRequest(context, AssetImages.svg.uri) {
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

        val key1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.svg.uri) {
            svgCss("css1")
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.svg.uri) {
            svgCss("css1")
        }.toRequestContext(sketch).cacheKey
        Assert.assertNotEquals(cacheKey1, cacheKey2)
    }
}