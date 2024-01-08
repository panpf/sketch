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
package com.github.panpf.sketch.extensions.core.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ignorePauseLoadWhenScrolling
import com.github.panpf.sketch.request.isIgnoredPauseLoadWhenScrolling
import com.github.panpf.sketch.request.isPauseLoadWhenScrolling
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PauseLoadWhenScrollingExtensionsTest {

    @Test
    fun testPauseLoadWhenScrolling() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        ImageOptions {
            pauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isPauseLoadWhenScrolling)
        }
        ImageOptions {
            pauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isPauseLoadWhenScrolling)
        }

        val key1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.svg.uri) {
            pauseLoadWhenScrolling()
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.svg.uri) {
            pauseLoadWhenScrolling(true)
        }.toRequestContext(sketch).cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testIgnorePauseLoadWhenScrolling() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, "http://sample.com/sample.jpeg").apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageRequest(context, "http://sample.com/sample.jpeg") {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions().apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        ImageOptions {
            ignorePauseLoadWhenScrolling()
        }.apply {
            Assert.assertTrue(isIgnoredPauseLoadWhenScrolling)
        }
        ImageOptions {
            ignorePauseLoadWhenScrolling(false)
        }.apply {
            Assert.assertFalse(isIgnoredPauseLoadWhenScrolling)
        }

        val key1 = ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.svg.uri) {
            ignorePauseLoadWhenScrolling()
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.svg.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.svg.uri) {
            ignorePauseLoadWhenScrolling(true)
        }.toRequestContext(sketch).cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }
}