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
package com.github.panpf.sketch.animated.android.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.animated.android.test.internal.TranslucentAnimatedTransformation
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimatedExtensionsAndroidTest {

    @Test
    fun testAnimatedTransformation() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val myAnimatedTransformation = TranslucentAnimatedTransformation

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animatedTransformation)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            this.animatedTransformation(myAnimatedTransformation)
        }.apply {
            assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        ImageRequest(context, MyImages.animGif.uri).apply {
            assertNull(animatedTransformation)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            animatedTransformation(myAnimatedTransformation)
        }.apply {
            assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        ImageOptions().apply {
            assertNull(animatedTransformation)
        }
        ImageOptions {
            animatedTransformation(myAnimatedTransformation)
        }.apply {
            assertEquals(myAnimatedTransformation, animatedTransformation)
        }

        val key1 = ImageRequest(context, MyImages.animGif.uri).key
        val key2 = ImageRequest(context, MyImages.animGif.uri) {
            animatedTransformation(myAnimatedTransformation)
        }.key
        assertNotEquals(key1, key2)

        val cacheKey1 =
            ImageRequest(context, MyImages.animGif.uri).toRequestContext(sketch).cacheKey
        val cacheKey2 = ImageRequest(context, MyImages.animGif.uri) {
            animatedTransformation(myAnimatedTransformation)
        }.toRequestContext(sketch).cacheKey
        assertEquals(cacheKey1, cacheKey2)
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