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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.test.utils.TestAssets
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RequestContextTest {

    @Test
    fun testRequests() {
        val context = getTestContext()
        val request1 = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI)
        val request2 = LoadRequest(context, TestAssets.SAMPLE_BMP_URI)
        val request3 = LoadRequest(context, TestAssets.SAMPLE_PNG_URI)

        Assert.assertNotEquals(request1, request2)
        Assert.assertNotEquals(request1, request3)
        Assert.assertNotEquals(request2, request3)

        RequestContext(request1).apply {
            Assert.assertEquals(request1, firstRequest)
            Assert.assertEquals(request1, lastRequest)
            Assert.assertEquals(listOf(request1), requests)

            addRequest(request1)
            Assert.assertEquals(request1, firstRequest)
            Assert.assertEquals(request1, lastRequest)
            Assert.assertEquals(listOf(request1), requests)

            addRequest(request2)
            Assert.assertEquals(request1, firstRequest)
            Assert.assertEquals(request2, lastRequest)
            Assert.assertEquals(listOf(request1, request2), requests)

            addRequest(request3)
            Assert.assertEquals(request1, firstRequest)
            Assert.assertEquals(request3, lastRequest)
            Assert.assertEquals(listOf(request1, request2, request3), requests)
        }
    }

    @Test
    fun testPendingCountDrawable() {
        val (context, sketch) = getTestContextAndNewSketch()
        val countDrawable = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = CountBitmap(
                cacheKey = "requestCacheKey",
                bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
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
                bitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
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
        val request = LoadRequest(context, TestAssets.SAMPLE_JPEG_URI)

        RequestContext(request).apply {
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