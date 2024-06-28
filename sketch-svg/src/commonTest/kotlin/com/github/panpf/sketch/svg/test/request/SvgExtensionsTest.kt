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

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class SvgExtensionsTest {

    @Test
    fun testSvgBackgroundColor() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgBackgroundColor)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            this.svgBackgroundColor(TestColor.BLUE)
        }.apply {
            assertEquals(TestColor.BLUE, svgBackgroundColor)
        }

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgBackgroundColor)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            svgBackgroundColor(TestColor.BLUE)
        }.apply {
            assertEquals(TestColor.BLUE, svgBackgroundColor)
        }

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgBackgroundColor)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            svgBackgroundColor(TestColor.BLUE)
        }.apply {
            assertEquals(TestColor.BLUE, svgBackgroundColor)
        }

        ImageOptions().apply {
            assertNull(svgBackgroundColor)
        }
        ImageOptions {
            svgBackgroundColor(TestColor.BLUE)
        }.apply {
            assertEquals(TestColor.BLUE, svgBackgroundColor)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            svgBackgroundColor(TestColor.BLUE)
        }.key
        assertNotEquals(key1, key2)

        runTest {
            val cacheKey1 =
                ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
            val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
                svgBackgroundColor(TestColor.BLUE)
            }.toRequestContext(sketch).cacheKey
            assertNotEquals(cacheKey1, cacheKey2)
        }
    }

    @Test
    fun testSvgCss() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgCss)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            this.svgCss("css1")
        }.apply {
            assertEquals("css1", svgCss)
        }

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgCss)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            svgCss("css1")
        }.apply {
            assertEquals("css1", svgCss)
        }

        ImageRequest(context, ResourceImages.svg.uri).apply {
            assertNull(svgCss)
        }
        ImageRequest(context, ResourceImages.svg.uri) {
            svgCss("css1")
        }.apply {
            assertEquals("css1", svgCss)
        }

        ImageOptions().apply {
            assertNull(svgCss)
        }
        ImageOptions {
            svgCss("css1")
        }.apply {
            assertEquals("css1", svgCss)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            svgCss("css1")
        }.key
        assertNotEquals(key1, key2)

        runTest {
            val cacheKey1 =
                ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
            val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
                svgCss("css1")
            }.toRequestContext(sketch).cacheKey
            assertNotEquals(cacheKey1, cacheKey2)
        }
    }
}