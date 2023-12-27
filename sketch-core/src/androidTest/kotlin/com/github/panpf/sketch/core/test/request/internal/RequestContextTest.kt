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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.core.test.getTestContextAndNewSketch
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.Depth.LOCAL
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestContextTest {

    @Test
    fun testRequest() {
        val context = getTestContext()
        runBlocking {
            val request0 = LoadRequest(context, AssetImages.jpeg.uri)
            request0.toRequestContext().apply {
                Assert.assertSame(request0, request)
                Assert.assertEquals(listOf(request0), requestList)

                val request1 = request0.newLoadRequest()
                setNewRequest(request1)
                Assert.assertSame(request0, request)
                Assert.assertEquals(listOf(request0), requestList)

                val request2 = request0.newLoadRequest {
                    depth(LOCAL)
                }
                setNewRequest(request2)
                Assert.assertSame(request2, request)
                Assert.assertEquals(listOf(request0, request2), requestList)

                val request3 = request2.newLoadRequest {
                    memoryCachePolicy(DISABLED)
                }
                setNewRequest(request3)
                Assert.assertSame(request3, request)
                Assert.assertEquals(listOf(request0, request2, request3), requestList)
            }
        }
    }

    @Test
    fun testKey() {
        val context = getTestContext()
        runBlocking {
            DisplayRequest(context, AssetImages.jpeg.uri).toRequestContext().apply {
                val key0 = key

                setNewRequest(request.newRequest())
                val key1 = key
                Assert.assertSame(key0, key1)

                setNewRequest(request.newRequest {
                    resizeSize(100, 300)
                })
                val key2 = key
                Assert.assertNotEquals(key1, key2)

                setNewRequest(request.newRequest {
                    bitmapConfig(RGB_565)
                })
                val key3 = key
                Assert.assertNotEquals(key2, key3)

                setNewRequest(request.newRequest())
                val key4 = key
                Assert.assertSame(key3, key4)

                setNewRequest(request.newRequest {
                    memoryCachePolicy(ENABLED)
                })
                val key5 = key
                Assert.assertNotSame(key4, key5)
                Assert.assertEquals(key4, key5)

                setNewRequest(request.newRequest {
                    memoryCachePolicy(DISABLED)
                })
                val key6 = key
                Assert.assertNotEquals(key5, key6)
            }
        }
    }

    @Test
    fun testCacheKey() {
        val context = getTestContext()
        runBlocking {
            DisplayRequest(context, AssetImages.jpeg.uri).toRequestContext().apply {
                val cacheKey0 = cacheKey

                setNewRequest(request.newRequest())
                val cacheKey1 = cacheKey
                Assert.assertSame(cacheKey0, cacheKey1)

                setNewRequest(request.newRequest {
                    resizeSize(100, 300)
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

                setNewRequest(request.newRequest {
                    ignoreExifOrientation(false)
                })
                val cacheKey5 = cacheKey
                Assert.assertNotSame(cacheKey4, cacheKey5)
                Assert.assertEquals(cacheKey4, cacheKey5)

                setNewRequest(request.newRequest {
                    ignoreExifOrientation(true)
                })
                val cacheKey6 = cacheKey
                Assert.assertNotEquals(cacheKey5, cacheKey6)
            }
        }
    }

    @Test
    fun testPendingCountDrawable() {
        val (context, sketch) = getTestContextAndNewSketch()
        val countDrawable = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = CountBitmap(
                cacheKey = "requestCacheKey",
                originBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                bitmapPool = sketch.bitmapPool,
                disallowReuseBitmap = false,
            ),
            imageUri = "imageUri",
            requestKey = "requestKey",
            requestCacheKey = "requestCacheKey",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = NETWORK
        )
        val countDrawable1 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = CountBitmap(
                cacheKey = "requestCacheKey1",
                originBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
                bitmapPool = sketch.bitmapPool,
                disallowReuseBitmap = false,
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = NETWORK
        )
        val request = LoadRequest(context, AssetImages.jpeg.uri)

        request.toRequestContext().apply {
            assertThrow(IllegalStateException::class) {
                pendingCountDrawable(countDrawable, "test")
            }
            assertThrow(IllegalStateException::class) {
                completeCountDrawable("test")
            }

            runBlocking(Dispatchers.Main) {
                completeCountDrawable("test")

                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable, "test")
                Assert.assertEquals(1, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable1, "test")
                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(1, countDrawable1.countBitmap.getPendingCount())

                pendingCountDrawable(countDrawable, "test")
                Assert.assertEquals(1, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())

                completeCountDrawable("test")
                Assert.assertEquals(0, countDrawable.countBitmap.getPendingCount())
                Assert.assertEquals(0, countDrawable1.countBitmap.getPendingCount())
            }
        }
    }
}