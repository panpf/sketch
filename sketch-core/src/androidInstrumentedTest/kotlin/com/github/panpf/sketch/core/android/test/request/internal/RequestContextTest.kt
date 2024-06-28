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
package com.github.panpf.sketch.core.android.test.request.internal

import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestContextTest {

    @Test
    fun testRequest() {
        val (context, sketch) = getTestContextAndSketch()
        runBlocking {
            val request0 = ImageRequest(context, ResourceImages.jpeg.uri)
            request0.toRequestContext(sketch).apply {
                Assert.assertSame(request0, request)
                Assert.assertEquals(listOf(request0), requestList)

                val request1 = request0.newRequest()
                setNewRequest(request1)
                Assert.assertSame(request0, request)
                Assert.assertEquals(listOf(request0), requestList)

                val request2 = request0.newRequest {
                    depth(LOCAL)
                }
                setNewRequest(request2)
                Assert.assertSame(request2, request)
                Assert.assertEquals(listOf(request0, request2), requestList)

                val request3 = request2.newRequest {
                    memoryCachePolicy(DISABLED)
                }
                setNewRequest(request3)
                Assert.assertSame(request3, request)
                Assert.assertEquals(listOf(request0, request2, request3), requestList)
            }
        }
    }

    // TODO test logKey

    @Test
    fun testCacheKey() {
        val (context, sketch) = getTestContextAndSketch()
        runBlocking {
            ImageRequest(context, ResourceImages.jpeg.uri).toRequestContext(sketch).apply {
                val cacheKey0 = cacheKey

                setNewRequest(request.newRequest())
                val cacheKey1 = cacheKey
                Assert.assertSame(cacheKey0, cacheKey1)

                setNewRequest(request.newRequest {
                    size(100, 300)
                })
                val cacheKey2 = cacheKey
                Assert.assertNotEquals(cacheKey1, cacheKey2)

                setNewRequest(request.newRequest {
                    bitmapConfig(RGB_565)
                })
                val cacheKey3 = cacheKey
                Assert.assertNotEquals(cacheKey2, cacheKey3)

                setNewRequest(request.newRequest())
                val cacheKey4 = cacheKey
                Assert.assertSame(cacheKey3, cacheKey4)
            }
        }
    }
}