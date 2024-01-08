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
package com.github.panpf.sketch.gif.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
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
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(repeatCount)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageRequest(context, AssetImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        assertThrow(IllegalArgumentException::class) {
            ImageRequest(context, AssetImages.animGif.uri) {
                repeatCount(-2)
            }
        }
        ImageRequest(context, AssetImages.animGif.uri) {
            this.repeatCount(5)
        }.apply {
            Assert.assertEquals(5, repeatCount)
        }

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(repeatCount)
        }
        assertThrow(IllegalArgumentException::class) {
            ImageRequest(context, AssetImages.animGif.uri) {
                this.repeatCount(-2)
            }
        }
        ImageRequest(context, AssetImages.animGif.uri) {
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

        val key1 = ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.animGif.uri) {
            repeatCount(5)
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.animGif.uri) {
            repeatCount(5)
        }.toRequestContext(sketch).cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationStart() {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationStartCallback = {}

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animationStartCallback)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
            this.onAnimationStart(myAnimationStartCallback)
        }.apply {
            Assert.assertEquals(myAnimationStartCallback, animationStartCallback)
        }

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animationStartCallback)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
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

        val key1 = ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.animGif.uri) {
            onAnimationStart(myAnimationStartCallback)
        }.toRequestContext(sketch).cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testOnAnimationEnd() {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimationEndCallback = {}

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animationEndCallback)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
            this.onAnimationEnd(myAnimationEndCallback)
        }.apply {
            Assert.assertEquals(myAnimationEndCallback, animationEndCallback)
        }

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animationEndCallback)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
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

        val key1 = ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.animGif.uri) {
            onAnimationEnd(myAnimationEndCallback)
        }.toRequestContext(sketch).cacheKey
        Assert.assertEquals(cacheKey1, cacheKey2)
    }

    @Test
    fun testAnimatedTransformation() {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimatedTransformation = AnimatedTransformation { PixelOpacity.TRANSLUCENT }

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animatedTransformation)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
            this.animatedTransformation(myAnimatedTransformation)
        }.apply {
            Assert.assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        ImageRequest(context, AssetImages.animGif.uri).apply {
            Assert.assertNull(animatedTransformation)
        }
        ImageRequest(context, AssetImages.animGif.uri) {
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

        val key1 = ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).key
        val key2 = ImageRequest(context, AssetImages.animGif.uri) {
            animatedTransformation(myAnimatedTransformation)
        }.toRequestContext(sketch).key
        Assert.assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, AssetImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, AssetImages.animGif.uri) {
            animatedTransformation(myAnimatedTransformation)
        }.toRequestContext(sketch).cacheKey
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