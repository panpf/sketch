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
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ALLOW_SET_NULL_DRAWABLE_KEY
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.allowSetNullDrawable
import com.github.panpf.sketch.test.utils.toRequestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllowSetNullDrawableExtensionsTest {

    @Test
    fun testAllowSetNullDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context

        (DisplayRequest(context, newAssetUri("sample_anim.gif")) as ImageRequest).apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).allowSetNullDrawable()
        } as ImageRequest).apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        (DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            (this as ImageRequest.Builder).allowSetNullDrawable(false)
        } as ImageRequest).apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        DisplayRequest(context, newAssetUri("sample_anim.gif")).apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            allowSetNullDrawable()
        }.apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        DisplayRequest(context, newAssetUri("sample_anim.gif")) {
            allowSetNullDrawable(false)
        }.apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        ImageOptions().apply {
            Assert.assertFalse(allowSetNullDrawable)
        }
        ImageOptions {
            allowSetNullDrawable()
        }.apply {
            Assert.assertTrue(allowSetNullDrawable)
        }
        ImageOptions {
            allowSetNullDrawable(false)
        }.apply {
            Assert.assertFalse(allowSetNullDrawable)
        }

        LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().key.apply {
            Assert.assertFalse(contains(ALLOW_SET_NULL_DRAWABLE_KEY))
        }
        LoadRequest(context, newAssetUri("sample_anim.gif")) {
            allowSetNullDrawable()
        }.toRequestContext().key.apply {
            Assert.assertTrue(contains(ALLOW_SET_NULL_DRAWABLE_KEY))
        }

        LoadRequest(context, newAssetUri("sample_anim.gif")).toRequestContext().cacheKey.apply {
            Assert.assertFalse(contains(ALLOW_SET_NULL_DRAWABLE_KEY))
        }
        LoadRequest(context, newAssetUri("sample_anim.gif")) {
            allowSetNullDrawable()
        }.toRequestContext().cacheKey.apply {
            Assert.assertFalse(contains(ALLOW_SET_NULL_DRAWABLE_KEY))
        }
    }
}