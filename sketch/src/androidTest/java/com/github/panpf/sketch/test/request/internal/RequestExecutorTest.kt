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
package com.github.panpf.sketch.test.request.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.WRITE_ONLY
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.internal.RequestExecutor
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestExecutorTest {

    @Test
    fun testGlobalImageOptions() {
        val (context, sketch) = getTestContextAndNewSketch {
            globalImageOptions(ImageOptions {
                depth(MEMORY)
                downloadCachePolicy(WRITE_ONLY)
            })
        }

        val request = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI).apply {
            Assert.assertEquals(Depth.NETWORK, depth)
            Assert.assertEquals(CachePolicy.ENABLED, downloadCachePolicy)
        }
        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(sketch, request, false).let { it as LoadResult }.apply {
                Assert.assertEquals(MEMORY, this.request.depth)
                Assert.assertEquals(WRITE_ONLY, this.request.downloadCachePolicy)
            }
        }
    }

    @Test
    fun testErrorUri() {
        val (context, sketch) = getTestContextAndNewSketch()

        runBlocking(Dispatchers.Main) {
            RequestExecutor().execute(
                sketch,
                LoadRequest(context, TestAssets.SAMPLE_JPEG_URI),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Success)
            }

            RequestExecutor().execute(
                sketch,
                LoadRequest(context, ""),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Error)
            }

            RequestExecutor().execute(
                sketch,
                LoadRequest(context, "  "),
                false
            ).apply {
                Assert.assertTrue(this is LoadResult.Error)
            }
        }
    }
}