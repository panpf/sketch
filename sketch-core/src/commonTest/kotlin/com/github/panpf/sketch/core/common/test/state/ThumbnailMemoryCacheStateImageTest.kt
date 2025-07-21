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

import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.cache.newCacheValueExtras
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.div
import com.github.panpf.sketch.util.minus
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ThumbnailMemoryCacheStateImageTest {

    @Test
    fun testConstructor() {
        ThumbnailMemoryCacheStateImage().apply {
            assertNull(uri)
            assertNull(defaultImage)
            assertEquals(-1, maxMismatchCount)
        }
        ThumbnailMemoryCacheStateImage("uri").apply {
            assertEquals("uri", uri)
            assertNull(defaultImage)
            assertEquals(-1, maxMismatchCount)
        }
        ThumbnailMemoryCacheStateImage("uri", FakeStateImage()).apply {
            assertEquals("uri", uri)
            assertNotNull(defaultImage)
            assertEquals(-1, maxMismatchCount)
        }
        ThumbnailMemoryCacheStateImage("uri", FakeStateImage(), 10).apply {
            assertEquals("uri", uri)
            assertNotNull(defaultImage)
            assertEquals(10, maxMismatchCount)
        }
    }

    @Test
    fun testKey() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val uri = request.uri.toString()

        val defaultImage = FakeStateImage()
        ThumbnailMemoryCacheStateImage(uri, defaultImage).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache('$uri',${defaultImage.key},-1)",
                actual = key
            )
        }
        val defaultImage1 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        ThumbnailMemoryCacheStateImage(uri, defaultImage1, 10).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache('$uri',${defaultImage1.key},10)",
                actual = key
            )
        }
        ThumbnailMemoryCacheStateImage(null, null).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache(null,null,-1)",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val imageFile = ResourceImages.jpeg
        val request = ImageRequest(context, imageFile.uri)
        val requestContext = request.toRequestContext(sketch, Size(1080, 1920))
        val thumbnailSize = imageFile.size / 8f
        val thumbnailImage = createBitmap(thumbnailSize.width, thumbnailSize.height).asImage()
        val imageInfo = ImageInfo(imageFile.size, "image/jpeg")
        val resize = Resize(500, 500, LESS_PIXELS, Scale.CENTER_CROP)

        val memoryCache = sketch.memoryCache
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)

        // empty
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // defaultImage
        val defaultImage = FakeStateImage()
        assertEquals(
            expected = defaultImage.getImage(sketch, request, null),
            actual = ThumbnailMemoryCacheStateImage(defaultImage = defaultImage)
                .getImage(sketch, request, null)
        )

        // default uri
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(imageInfo, resize, transformeds = null, extras = null)
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // custom uri and startsWith error
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage(
                uri = imageFile.uri.replace("://", ".custom://")
            ).getImage(sketch, request, null)
        )

        // memoryCacheKey uri same
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = imageFile.uri,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(imageInfo, resize, transformeds = null, extras = null)
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // memoryCacheKey.length error
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = imageFile.uri + "!",
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(imageInfo, resize, transformeds = null, extras = null)
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // char1 error
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey.replace("?_", "__"),
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(imageInfo, resize, transformeds = null, extras = null)
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // char2 error
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey.replace("?_", "??"),
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(imageInfo, resize, transformeds = null, extras = null)
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // size same
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = ImageInfo(thumbnailSize, "image/jpeg"),
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // image size greater than thumbnail size 1
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = ImageInfo(thumbnailSize - Size(1, 0), "image/jpeg"),
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // image size greater than thumbnail size 2
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = ImageInfo(thumbnailSize - Size(0, 1), "image/jpeg"),
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // thumbnail size within the error range
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        val thumbnailImage2 = createBitmap(thumbnailSize.width + 2, thumbnailSize.height).asImage()
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage2,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage2,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // thumbnail size without the error range
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        val thumbnailImage3 = createBitmap(thumbnailSize.width + 3, thumbnailSize.height).asImage()
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage3,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // transformeds
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = listOf(createInSampledTransformed(4)),
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // transformeds error
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey,
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = listOf(
                        createInSampledTransformed(4),
                        createResizeTransformed(resize)
                    ),
                    extras = null
                )
            )
        )
        assertEquals(expected = 1, actual = memoryCache.keys().size)
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )

        // maxMismatchCount

        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)
        memoryCache.put(
            key = requestContext.memoryCacheKey + "&_test=1",
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = listOf(
                        createInSampledTransformed(4),
                        createResizeTransformed(resize)
                    ),
                    extras = null
                )
            )
        )
        memoryCache.put(
            key = requestContext.memoryCacheKey + "&_test=2",
            value = ImageCacheValue(
                image = thumbnailImage3,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        memoryCache.put(
            key = requestContext.memoryCacheKey + "&_test=3",
            value = ImageCacheValue(
                image = thumbnailImage,
                extras = newCacheValueExtras(
                    imageInfo = imageInfo,
                    resize = resize,
                    transformeds = null,
                    extras = null
                )
            )
        )
        assertEquals(3, memoryCache.keys().size)
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage().getImage(sketch, request, null)
        )
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage(maxMismatchCount = 0).getImage(
                sketch,
                request,
                null
            )
        )
        assertEquals(
            expected = null,
            actual = ThumbnailMemoryCacheStateImage(maxMismatchCount = 1).getImage(
                sketch,
                request,
                null
            )
        )
        assertEquals(
            expected = thumbnailImage,
            actual = ThumbnailMemoryCacheStateImage(maxMismatchCount = 2).getImage(
                sketch,
                request,
                null
            )
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 =
            ThumbnailMemoryCacheStateImage("uri1", FakeStateImage())
        val element11 =
            ThumbnailMemoryCacheStateImage("uri1", FakeStateImage())
        val element2 =
            ThumbnailMemoryCacheStateImage("uri1", FakeStateImage(FakeImage(SketchSize(200, 200))))
        val element3 =
            ThumbnailMemoryCacheStateImage("uri2", FakeStateImage(FakeImage(SketchSize(300, 300))))
        val element4 =
            ThumbnailMemoryCacheStateImage(null, FakeStateImage(FakeImage(SketchSize(400, 400))))
        val element5 = ThumbnailMemoryCacheStateImage("uri1", null)
        val element6 = ThumbnailMemoryCacheStateImage("uri1", null, 10)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element1, element6)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element2, element6)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element3, element6)
        assertNotEquals(element4, element5)
        assertNotEquals(element4, element6)
        assertNotEquals(element5, element6)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element1.hashCode(), element6.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element6.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element6.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element6.hashCode())
        assertNotEquals(element5.hashCode(), element6.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val uri = request.uri.toString()

        val defaultImage = FakeStateImage()
        ThumbnailMemoryCacheStateImage(uri, defaultImage).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri='$uri', defaultImage=$defaultImage, maxMismatchCount=-1)",
                toString()
            )
        }
        val defaultImage1 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        ThumbnailMemoryCacheStateImage(uri, defaultImage1, 10).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri='$uri', defaultImage=$defaultImage1, maxMismatchCount=10)",
                toString()
            )
        }
        ThumbnailMemoryCacheStateImage(null, null).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri=null, defaultImage=null, maxMismatchCount=-1)",
                toString()
            )
        }
    }
}