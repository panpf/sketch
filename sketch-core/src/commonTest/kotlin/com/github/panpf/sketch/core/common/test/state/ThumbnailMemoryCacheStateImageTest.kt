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

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.TestCountTarget
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.util.SketchSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ThumbnailMemoryCacheStateImageTest {

    @Test
    fun testKey() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val uri = request.uri.toString()

        val defaultImage = FakeStateImage()
        ThumbnailMemoryCacheStateImage(uri, defaultImage).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache('$uri',${defaultImage.key})",
                actual = key
            )
        }
        val defaultImage1 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        ThumbnailMemoryCacheStateImage(uri, defaultImage1).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache('$uri',${defaultImage1.key})",
                actual = key
            )
        }
        ThumbnailMemoryCacheStateImage(null, null).apply {
            assertEquals(
                expected = "ThumbnailMemoryCache(null,null)",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val memoryCache = sketch.memoryCache
        memoryCache.clear()
        assertEquals(0, memoryCache.keys().size)

        val requests1 = arrayOf(
            ImageRequest(context, ResourceImages.jpeg.uri) {
                size(100, 100)
                precision(LESS_PIXELS)
                target(TestCountTarget())
            },
            ImageRequest(context, ResourceImages.jpeg.uri) {
                size(100, 100)
                precision(EXACTLY)
                target(TestCountTarget())
            },
            ImageRequest(context, ResourceImages.jpeg.uri) {
                size(100, 100)
                precision(LESS_PIXELS)
                target(TestCountTarget())
                transformations(CircleCropTransformation())
            },
        )
        val requests2 = arrayOf(
            ImageRequest(context, ResourceImages.png.uri) {
                size(100, 100)
                precision(LESS_PIXELS)
                target(TestCountTarget())
            },
            ImageRequest(context, ResourceImages.png.uri) {
                size(100, 100)
                precision(EXACTLY)
                target(TestCountTarget())
            },
            ImageRequest(context, ResourceImages.png.uri) {
                size(100, 100)
                precision(LESS_PIXELS)
                target(TestCountTarget())
                transformations(CircleCropTransformation())
            },
        )

        assertEquals(
            6,
            requests1.map { it.toRequestContext(sketch).memoryCacheKey }
                .plus(requests2.map { it.toRequestContext(sketch).memoryCacheKey }).distinct().size
        )

        withContext(Dispatchers.Main) {
            val inexactlyStateImage = ThumbnailMemoryCacheStateImage()
            val inexactlyStateImage1 = ThumbnailMemoryCacheStateImage(requests1[0].uri.toString())
            val inexactlyStateImage2 = ThumbnailMemoryCacheStateImage(requests2[0].uri.toString())

            assertEquals(0, memoryCache.keys().size)
            requests1.plus(requests2).forEach { request ->
                assertNull(
                    memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                    request.toRequestContext(sketch).memoryCacheKey,
                )
            }
            requests1.plus(requests2).forEach { request ->
                assertNull(
                    inexactlyStateImage.getImage(sketch, request, null),
                    request.toRequestContext(sketch).memoryCacheKey,
                )
                assertNull(
                    inexactlyStateImage1.getImage(sketch, request, null),
                    request.toRequestContext(sketch).memoryCacheKey,
                )
                assertNull(
                    inexactlyStateImage2.getImage(sketch, request, null),
                    request.toRequestContext(sketch).memoryCacheKey,
                )
            }

            val testRequests1: suspend (Int) -> Unit = { loadIndex ->
                memoryCache.clear()
                assertEquals(0, memoryCache.keys().size)
                sketch.enqueue(requests1[loadIndex]).job.await()
                assertEquals(1, memoryCache.keys().size)
                requests1.forEachIndexed { index, request ->
                    if (index == loadIndex) {
                        assertNotNull(
                            memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                }
                requests2.forEach { request ->
                    assertNull(
                        memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                        request.toRequestContext(sketch).memoryCacheKey,
                    )
                }
                requests1.forEach { request ->
                    if (loadIndex == 0) {
                        assertNotNull(
                            inexactlyStateImage.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNotNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            inexactlyStateImage.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                }
                requests2.forEach { request ->
                    assertNull(inexactlyStateImage.getImage(sketch, request, null))
                    if (loadIndex == 0) {
                        assertNotNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                    assertNull(
                        inexactlyStateImage2.getImage(sketch, request, null),
                        request.toRequestContext(sketch).memoryCacheKey,
                    )
                }
            }
            testRequests1(0)
            testRequests1(1)
            testRequests1(2)

            val testRequests2: suspend (Int) -> Unit = { loadIndex ->
                memoryCache.clear()
                assertEquals(0, memoryCache.keys().size)
                sketch.enqueue(requests2[loadIndex]).job.await()
                assertEquals(1, memoryCache.keys().size)
                requests1.forEach { request ->
                    assertNull(
                        memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                        request.toRequestContext(sketch).memoryCacheKey,
                    )
                }
                requests2.forEachIndexed { index, request ->
                    if (index == loadIndex) {
                        assertNotNull(
                            memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            memoryCache[request.toRequestContext(sketch).memoryCacheKey],
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                }
                requests1.forEach { request ->
                    assertNull(inexactlyStateImage.getImage(sketch, request, null))
                    assertNull(
                        inexactlyStateImage1.getImage(sketch, request, null),
                        request.toRequestContext(sketch).memoryCacheKey,
                    )
                    if (loadIndex == 0) {
                        assertNotNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                }
                requests2.forEach { request ->
                    if (loadIndex == 0) {
                        assertNotNull(
                            inexactlyStateImage.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNotNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    } else {
                        assertNull(
                            inexactlyStateImage.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage1.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                        assertNull(
                            inexactlyStateImage2.getImage(sketch, request, null),
                            request.toRequestContext(sketch).memoryCacheKey,
                        )
                    }
                }
            }
            testRequests2(0)
            testRequests2(1)
            testRequests2(2)

            memoryCache.clear()
            assertEquals(0, memoryCache.keys().size)
            sketch.enqueue(requests1[0]).job.await()
            sketch.enqueue(requests2[0]).job.await()
            assertEquals(2, memoryCache.keys().size)
            requests1.forEachIndexed { index, request ->
                if (index == 0) {
                    assertNotNull(memoryCache[request.toRequestContext(sketch).memoryCacheKey])
                } else {
                    assertNull(memoryCache[request.toRequestContext(sketch).memoryCacheKey])
                }
            }
            requests2.forEachIndexed { index, request ->
                if (index == 0) {
                    assertNotNull(memoryCache[request.toRequestContext(sketch).memoryCacheKey])
                } else {
                    assertNull(memoryCache[request.toRequestContext(sketch).memoryCacheKey])
                }
            }
            requests1.forEach { request ->
                assertNotNull(inexactlyStateImage.getImage(sketch, request, null))
                assertNotNull(inexactlyStateImage1.getImage(sketch, request, null))
                assertNotNull(inexactlyStateImage2.getImage(sketch, request, null))
            }
            requests2.forEach { request ->
                assertNotNull(inexactlyStateImage.getImage(sketch, request, null))
                assertNotNull(inexactlyStateImage1.getImage(sketch, request, null))
                assertNotNull(inexactlyStateImage2.getImage(sketch, request, null))
            }

            memoryCache.clear()
            assertEquals(0, memoryCache.keys().size)
            sketch.enqueue(requests1[1]).job.await()
            sketch.enqueue(requests1[2]).job.await()
            sketch.enqueue(requests1[2].newRequest {
                transformations(listOf(RoundedCornersTransformation()))
            }).job.await()
            sketch.enqueue(requests1[2].newRequest {
                transformations(listOf(RotateTransformation(90)))
            }).job.await()
            assertNull(inexactlyStateImage.getImage(sketch, requests1[0], null))
        }
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
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val uri = request.uri.toString()

        val defaultImage = FakeStateImage()
        ThumbnailMemoryCacheStateImage(uri, defaultImage).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri='$uri', defaultImage=$defaultImage)",
                toString()
            )
        }
        val defaultImage1 = FakeStateImage(FakeImage(SketchSize(200, 200)))
        ThumbnailMemoryCacheStateImage(uri, defaultImage1).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri='$uri', defaultImage=$defaultImage1)",
                toString()
            )
        }
        ThumbnailMemoryCacheStateImage(null, null).apply {
            assertEquals(
                "ThumbnailMemoryCacheStateImage(uri=null, defaultImage=null)",
                toString()
            )
        }
    }
}