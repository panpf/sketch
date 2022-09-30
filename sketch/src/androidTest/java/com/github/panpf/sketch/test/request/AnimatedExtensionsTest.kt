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
package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimatedExtensionsTest {

    @Test
    fun testRepeatCount() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample_anim.gif")) as ImageRequest).apply {
            Assert.assertNull(repeatCount)
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample_anim.gif")) {
                (this as ImageRequest.Builder).repeatCount(-2)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample_anim.gif")) {
                repeatCount(-2)
            }
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).repeatCount(5)
        } as ImageRequest).apply {
            Assert.assertEquals(5, repeatCount)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).apply {
            Assert.assertNull(repeatCount)
        }
        assertThrow(IllegalArgumentException::class) {
            DisplayRequest(context, newAssetUri("sample_anim.gif")) {
                (this as ImageRequest.Builder).repeatCount(-2)
            }
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            repeatCount(5)
        }.apply {
            Assert.assertEquals(5, repeatCount)
        }

        ImageOptions().apply {
            Assert.assertNull(repeatCount)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageOptions {
                repeatCount(-2)
            }
        }
        ImageOptions {
            repeatCount(5)
        }.apply {
            Assert.assertEquals(5, repeatCount)
        }

        val key1 = LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            repeatCount(5)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            repeatCount(5)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationStart() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val myAnimationStartCallback = {}

        (DisplayRequest(context, newAssetUri("sample_anim.gif")) as ImageRequest).apply {
            Assert.assertNull(animationStartCallback)
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).onAnimationStart(myAnimationStartCallback)
        } as ImageRequest).apply {
            Assert.assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).apply {
            Assert.assertNull(animationStartCallback)
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            Assert.assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageOptions().apply {
            Assert.assertNull(animationStartCallback)
        }
        ImageOptions {
            onAnimationStart(myAnimationStartCallback)
        }.apply {
            Assert.assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        val key1 = LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationEnd() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val myAnimationEndCallback = {}

        (DisplayRequest(context, newAssetUri("sample_anim.gif")) as ImageRequest).apply {
            Assert.assertNull(animationEndCallback)
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).onAnimationEnd(myAnimationEndCallback)
        } as ImageRequest).apply {
            Assert.assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).apply {
            Assert.assertNull(animationEndCallback)
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            Assert.assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageOptions().apply {
            Assert.assertNull(animationEndCallback)
        }
        ImageOptions {
            onAnimationEnd(myAnimationEndCallback)
        }.apply {
            Assert.assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        val key1 = LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testAnimatedTransformation() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val myAnimatedTransformation = AnimatedTransformation { PixelOpacity.TRANSLUCENT }

        (DisplayRequest(context, newAssetUri("sample_anim.gif")) as ImageRequest).apply {
            Assert.assertNull(animatedTransformation)
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).animatedTransformation(myAnimatedTransformation)
        } as ImageRequest).apply {
            Assert.assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).apply {
            Assert.assertNull(animatedTransformation)
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            animatedTransformation(myAnimatedTransformation)
        }.apply {
            Assert.assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        ImageOptions().apply {
            Assert.assertNull(animatedTransformation)
        }
        ImageOptions {
            animatedTransformation(myAnimatedTransformation)
        }.apply {
            Assert.assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        val key1 = LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().key
        val key2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            animatedTransformation(myAnimatedTransformation)
        }.toRequestContext().key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().cacheKey
        val cacheKey2 = LoadRequest(context, newAssetUri("sample_anim.gif")) {
            animatedTransformation(myAnimatedTransformation)
        }.toRequestContext().cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testAnimatable2CompatCallbackOf() {
        animatable2CompatCallbackOf(onStart = null, onEnd = null).apply {
            onAnimationStart(null)
            onAnimationEnd(null)
        }

        animatable2CompatCallbackOf(onStart = {}, onEnd = { }).apply {
            onAnimationStart(null)
            onAnimationEnd(null)
        }
    }
}