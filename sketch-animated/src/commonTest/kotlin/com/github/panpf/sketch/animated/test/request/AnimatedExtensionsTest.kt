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
package com.github.panpf.sketch.animated.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AnimatedExtensionsTest {

    @Test
    fun testRepeatCount() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageRequest(context, MyImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        assertFails {
            ImageRequest(context, MyImages.animGif.uri) {
                repeatCount(-2)
            }
        }
        ImageRequest(context, MyImages.animGif.uri) {
            this.repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageRequest(context, MyImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        ImageRequest(context, MyImages.animGif.uri) {
            repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        ImageOptions().apply {
            assertNull(repeatCount)
        }
        assertFails {
            ImageOptions {
                repeatCount(-2)
            }
        }
        ImageOptions {
            repeatCount(5)
        }.apply {
            assertEquals(5, repeatCount)
        }

        val key1 = ImageRequest(context, MyImages.animGif.uri).key
        val key2 = ImageRequest(context, MyImages.animGif.uri) {
            repeatCount(5)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, MyImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, MyImages.animGif.uri) {
            repeatCount(5)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationStart() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationStartCallback = {}

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animationStartCallback)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            this.onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animationStartCallback)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageOptions().apply {
            assertNull(animationStartCallback)
        }
        ImageOptions {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        val key1 = ImageRequest(context, MyImages.animGif.uri).key
        val key2 = ImageRequest(context, MyImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, MyImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, MyImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationEnd() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationEndCallback = {}

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animationEndCallback)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            this.onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animationEndCallback)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageOptions().apply {
            assertNull(animationEndCallback)
        }
        ImageOptions {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        val key1 = ImageRequest(context, MyImages.animGif.uri).key
        val key2 = ImageRequest(context, MyImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, MyImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, MyImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
    }
}