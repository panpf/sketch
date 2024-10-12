/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.core.common.test.state

import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.MemoryCacheStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.createBitmapImage
import com.github.panpf.sketch.test.utils.createCacheValue
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemoryCacheStateImageTest {

    @Test
    fun testKey() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val memoryCacheKey = request.toRequestContext(sketch).cacheKey
        val stateImage = FakeStateImage()

        MemoryCacheStateImage(null, null).apply {
            assertEquals(
                expected = "MemoryCache('null',null)",
                actual = key
            )
        }
        MemoryCacheStateImage(memoryCacheKey, stateImage).apply {
            assertEquals(
                expected = "MemoryCache('$memoryCacheKey',${stateImage.key})",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val memoryCache = sketch.memoryCache
        val memoryCacheKey = request.toRequestContext(sketch).cacheKey
        val stateImage = FakeStateImage()

        memoryCache.clear()
        assertFalse(memoryCache.exist(memoryCacheKey))

        MemoryCacheStateImage(null, null).apply {
            assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey).apply {
            assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, stateImage).apply {
            assertEquals(stateImage.image, getImage(sketch, request, null))
        }

        val newImage = createBitmapImage(100, 100)
        memoryCache.put(
            key = memoryCacheKey,
            value = createCacheValue(
                image = newImage,
                newCacheValueExtras(
                    imageInfo = ImageInfo(100, 100, "image/jpeg"),
                    resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                    transformeds = null,
                    extras = null,
                )
            )
        )
        assertTrue(memoryCache.exist(memoryCacheKey))
        MemoryCacheStateImage(null, null).apply {
            assertNull(getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, null).apply {
            assertEquals(newImage, getImage(sketch, request, null))
        }
        MemoryCacheStateImage(memoryCacheKey, stateImage).apply {
            assertEquals(newImage, getImage(sketch, request, null))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MemoryCacheStateImage("key1", FakeStateImage())
        val element11 = MemoryCacheStateImage("key1", FakeStateImage())
        val element2 =
            MemoryCacheStateImage("key1", FakeStateImage(FakeImage(SketchSize(200, 200))))
        val element3 =
            MemoryCacheStateImage("key2", FakeStateImage(FakeImage(SketchSize(300, 300))))
        val element4 = MemoryCacheStateImage(null, FakeStateImage(FakeImage(SketchSize(400, 400))))
        val element5 = MemoryCacheStateImage("key1", null)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val memoryCacheKey = request.toRequestContext(sketch).cacheKey
        val stateImage = FakeStateImage()

        MemoryCacheStateImage(null, null).apply {
            assertEquals(
                "MemoryCacheStateImage(cacheKey='null', defaultImage=null)",
                toString()
            )
        }
        MemoryCacheStateImage(memoryCacheKey, stateImage).apply {
            assertEquals(
                "MemoryCacheStateImage(cacheKey='$memoryCacheKey', defaultImage=$stateImage)",
                toString()
            )
        }
    }
}