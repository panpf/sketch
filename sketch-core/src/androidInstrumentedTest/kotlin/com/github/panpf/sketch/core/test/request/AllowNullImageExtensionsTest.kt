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
package com.github.panpf.sketch.core.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.ALLOW_NULL_IMAGE_KEY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.request.allowNullImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllowNullImageExtensionsTest {

    @Test
    fun testAllowNullImage() {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, MyImages.animGif.uri).apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable()
        }.apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable(false)
        }.apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        ImageRequest(context, MyImages.animGif.uri).apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable()
        }.apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable(false)
        }.apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        ImageOptions().apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        ImageOptions {
            allowNullImage()
        }.apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        ImageOptions {
            allowNullImage(false)
        }.apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        ImageRequest(context, MyImages.animGif.uri).key.apply {
            Assert.assertFalse(contains(ALLOW_NULL_IMAGE_KEY))
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable()
        }.key.apply {
            Assert.assertTrue(contains(ALLOW_NULL_IMAGE_KEY))
        }

        ImageRequest(context, MyImages.animGif.uri).toRequestContext(sketch).cacheKey.apply {
            Assert.assertFalse(contains(ALLOW_NULL_IMAGE_KEY))
        }
        ImageRequest(context, MyImages.animGif.uri) {
            allowSetNullDrawable()
        }.toRequestContext(sketch).cacheKey.apply {
            Assert.assertFalse(contains(ALLOW_NULL_IMAGE_KEY))
        }
    }
}