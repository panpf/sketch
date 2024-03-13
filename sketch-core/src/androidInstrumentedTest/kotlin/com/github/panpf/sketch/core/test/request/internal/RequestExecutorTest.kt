@file:Suppress("DEPRECATION")

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
package com.github.panpf.sketch.core.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.internal.DisplaySizeResolver
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestExecutorTest {

    @Test
    fun testErrorUri() {
        val (context, sketch) = getTestContextAndNewSketch()

        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(
                sketch,
                ImageRequest(context, AssetImages.jpeg.uri),
                false
            ).apply {
                Assert.assertTrue(this is ImageResult.Success)
            }

            RequestExecutor().execute(
                sketch,
                ImageRequest(context, ""),
                false
            ).apply {
                Assert.assertTrue(this is ImageResult.Error)
            }

            RequestExecutor().execute(
                sketch,
                ImageRequest(context, "  "),
                false
            ).apply {
                Assert.assertTrue(this is ImageResult.Error)
            }
        }
    }

    @Test
    fun testGlobalImageOptions() {
        val (context, sketch) = getTestContextAndNewSketch {
        }
        val request = ImageRequest(context, AssetImages.jpeg.uri).apply {
            Assert.assertEquals(Depth.NETWORK, depth)
            Assert.assertEquals(CachePolicy.ENABLED, downloadCachePolicy)
            Assert.assertTrue(sizeResolver is DisplaySizeResolver)
        }
        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(sketch, request, false).apply {
                Assert.assertEquals(Depth.NETWORK, this.request.depth)
                Assert.assertEquals(CachePolicy.ENABLED, this.request.downloadCachePolicy)
                Assert.assertTrue(this.request.sizeResolver is DisplaySizeResolver)
            }
        }

        val (context2, sketch2) = getTestContextAndNewSketch {
            globalImageOptions(ImageOptions {
                depth(MEMORY)
                downloadCachePolicy(WRITE_ONLY)
                resize(44, 67)
            })
        }
        val request2 = ImageRequest(context2, AssetImages.jpeg.uri).apply {
            Assert.assertEquals(Depth.NETWORK, depth)
            Assert.assertEquals(CachePolicy.ENABLED, downloadCachePolicy)
            Assert.assertTrue(sizeResolver is DisplaySizeResolver)
        }
        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(sketch2, request2, false).apply {
                Assert.assertEquals(MEMORY, this.request.depth)
                Assert.assertEquals(WRITE_ONLY, this.request.downloadCachePolicy)
                Assert.assertTrue(this.request.sizeResolver is FixedSizeResolver)
            }
        }
    }
}