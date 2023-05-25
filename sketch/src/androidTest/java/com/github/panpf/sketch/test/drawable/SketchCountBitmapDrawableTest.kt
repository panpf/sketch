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
package com.github.panpf.sketch.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchCountBitmapDrawableTest {

    @Test
    fun testProperty() {
        val (context, sketch) = getTestContextAndNewSketch()

        val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
        SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = CountBitmap(
                cacheKey = "requestCacheKey1",
                originBitmap = bitmap,
                bitmapPool = sketch.bitmapPool,
                disallowReuseBitmap = false,
            ),
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = MEMORY
        ).apply {
            Assert.assertEquals("imageUri1", imageUri)
            Assert.assertEquals("requestKey1", requestKey)
            Assert.assertEquals("requestCacheKey1", requestCacheKey)
            Assert.assertEquals(ImageInfo(100, 100, "image/jpeg", 0), imageInfo)
            Assert.assertEquals(null, transformedList)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val (context, sketch) = getTestContextAndNewSketch()
        val countBitmap = CountBitmap(
            cacheKey = "requestCacheKey1",
            originBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val countBitmap2 = CountBitmap(
            cacheKey = "requestCacheKey2",
            originBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        val element1 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = MEMORY
        )
        val element11 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = MEMORY
        )
        val element2 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap2,
            imageUri = "imageUri2",
            requestKey = "requestKey2",
            requestCacheKey = "requestCacheKey2",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = MEMORY
        )
        val element3 = SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = LOCAL
        )

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()

        val countBitmap = CountBitmap(
            cacheKey = "requestCacheKey1",
            originBitmap = Bitmap.createBitmap(100, 100, ARGB_8888),
            bitmapPool = sketch.bitmapPool,
            disallowReuseBitmap = false,
        )
        SketchCountBitmapDrawable(
            resources = context.resources,
            countBitmap = countBitmap,
            imageUri = "imageUri1",
            requestKey = "requestKey1",
            requestCacheKey = "requestCacheKey1",
            imageInfo = ImageInfo(100, 100, "image/jpeg", 0),
            transformedList = null,
            extras = null,
            dataFrom = MEMORY
        ).apply {
            Assert.assertEquals(
                "SketchCountBitmapDrawable(${countBitmap},${imageInfo.toShortString()},$MEMORY,null,null,'requestKey1')",
                toString()
            )
        }
    }
}