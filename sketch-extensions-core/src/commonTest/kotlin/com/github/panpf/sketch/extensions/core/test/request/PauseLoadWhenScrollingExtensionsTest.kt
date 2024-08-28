/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.extensions.core.test.request

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PauseLoadWhenScrollingExtensionsTest {

    @Test
    fun testPauseLoadWhenScrolling() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            assertFalse(isPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling()
        }.apply {
            assertTrue(isPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling()
        }.apply {
            assertTrue(isPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions {
            pauseLoadWhenScrolling()
        }.apply {
            assertTrue(isPauseLoadWhenScrolling)
        }
        ImageOptions {
            pauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isPauseLoadWhenScrolling)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            pauseLoadWhenScrolling()
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
            pauseLoadWhenScrolling(true)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnorePauseLoadWhenScrolling() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling()
        }.apply {
            assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling()
        }.apply {
            assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions {
            ignorePauseLoadWhenScrolling()
        }.apply {
            assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageOptions {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        val key1 = ImageRequest(context, ResourceImages.svg.uri).key
        val key2 = ImageRequest(context, ResourceImages.svg.uri) {
            ignorePauseLoadWhenScrolling()
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, ResourceImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, ResourceImages.svg.uri) {
            ignorePauseLoadWhenScrolling(true)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }
}