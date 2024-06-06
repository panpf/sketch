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
package com.github.panpf.sketch.core.android.test.state

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IntColorStateImage
import com.github.panpf.sketch.state.MemoryCacheStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryCacheStateImageTest {

    @Test
    fun testGetDrawable() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.toRequestContext(sketch).cacheKey

        memoryCache.clear()
        Assert.assertFalse(memoryCache.exist(memoryCacheKey))

        MemoryCacheStateImage(null, null).apply {
            Assert.assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey).apply {
            Assert.assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, IntColorStateImage(Color.BLUE)).apply {
            Assert.assertTrue(
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable is ColorDrawable
            )
        }

        // TODO test defaultImage

        memoryCache.put(
            key = memoryCacheKey,
            value = AndroidBitmapImageValue(
                image = Bitmap.createBitmap(100, 100, RGB_565).asSketchImage(),
                newCacheValueExtras(
                    imageInfo = ImageInfo(100, 100, "image/jpeg"),
                    transformedList = null,
                    extras = null,
                )
            )
        )

        Assert.assertTrue(memoryCache.exist(memoryCacheKey))

        MemoryCacheStateImage(null, null).apply {
            Assert.assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, null).apply {
            Assert.assertTrue(getImage(sketch, request, null) is AndroidBitmapImage)
        }
        MemoryCacheStateImage(memoryCacheKey, IntColorStateImage(Color.BLUE)).apply {
            Assert.assertTrue(getImage(sketch, request, null) is AndroidBitmapImage)
        }

        // TODO test defaultImage
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MemoryCacheStateImage("key1", IntColorStateImage(Color.BLUE))
        val element11 = MemoryCacheStateImage("key1", IntColorStateImage(Color.BLUE))
        val element2 = MemoryCacheStateImage("key1", IntColorStateImage(Color.GREEN))
        val element3 = MemoryCacheStateImage("key2", IntColorStateImage(Color.BLUE))
        val element4 = MemoryCacheStateImage(null, IntColorStateImage(Color.BLUE))
        val element5 = MemoryCacheStateImage("key1", null)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element1, element5)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element2, element5)
        Assert.assertNotSame(element3, element4)
        Assert.assertNotSame(element3, element5)
        Assert.assertNotSame(element4, element5)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element1, element5)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element2, element5)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element3, element5)
        Assert.assertNotEquals(element4, element5)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element5.hashCode())
        Assert.assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        val memoryCacheKey = request.toRequestContext(sketch).cacheKey

        MemoryCacheStateImage(memoryCacheKey, IntColorStateImage(Color.BLUE)).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(cacheKey=$memoryCacheKey, defaultImage=ColorStateImage(IntColor(${Color.BLUE})))",
                toString()
            )
        }
        MemoryCacheStateImage(memoryCacheKey, IntColorStateImage(Color.GREEN)).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(cacheKey=$memoryCacheKey, defaultImage=ColorStateImage(IntColor(${Color.GREEN})))",
                toString()
            )
        }
        MemoryCacheStateImage(null, null).apply {
            Assert.assertEquals(
                "MemoryCacheStateImage(cacheKey=null, defaultImage=null)",
                toString()
            )
        }
    }
}