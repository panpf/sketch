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

package com.github.panpf.sketch.core.common.test.transform.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.fetch.internal.FetcherInterceptor
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.internal.DecoderInterceptor
import com.github.panpf.sketch.request.internal.InterceptorChain
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.internal.TransformationInterceptor
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TransformationInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val interceptors = listOf(
            TransformationInterceptor(),
            FetcherInterceptor(),
            DecoderInterceptor()
        )

        runBlock {
            val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
            }
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request)
            }
        }.getOrThrow().apply {
            assertEquals(Size(1291, 1936), image.size)
            assertNotEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                image.corners()
            )
            assertNull(actual = transformeds)
        }

        runBlock {
            val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(CircleCropTransformation())
            }
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request)
            }
        }.getOrThrow().apply {
            assertEquals(Size(1291, 1291), image.size)
            assertEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                image.corners()
            )
            assertEquals(listOf(createCircleCropTransformed(CENTER_CROP)), transformeds)
        }

        runBlock {
            val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(object : Transformation {
                    override val key: String
                        get() = "TestTransformation"

                    override fun transform(
                        requestContext: RequestContext,
                        input: Image
                    ): TransformResult = TransformResult(input, "TestTransformation")

                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }

                    override fun hashCode(): Int {
                        return super.hashCode()
                    }

                    override fun toString(): String {
                        return super.toString()
                    }
                })
            }
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request)
            }
        }.getOrThrow().apply {
            assertEquals(Size(1291, 1936), image.size)
            assertNotEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                image.corners()
            )
            assertNotNull(transformeds)
        }

        runBlock {
            val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(object : Transformation {
                    override val key: String
                        get() = "TestTransformation"

                    override fun transform(
                        requestContext: RequestContext,
                        input: Image
                    ): TransformResult? = null

                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }

                    override fun hashCode(): Int {
                        return super.hashCode()
                    }

                    override fun toString(): String {
                        return super.toString()
                    }
                })
            }
            val chain = InterceptorChain(
                requestContext = request.toRequestContext(sketch),
                interceptors = interceptors,
                index = 0
            )
            withContext(Dispatchers.Main) {
                chain.proceed(request)
            }
        }.getOrThrow().apply {
            assertEquals(Size(1291, 1936), image.size)
            assertNotEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                image.corners()
            )
            assertNull(actual = transformeds)
        }
    }

    @Test
    fun testSortWeight() {
        assertEquals(
            expected = 75,
            actual = TransformationInterceptor().sortWeight
        )
        assertEquals(
            expected = 75,
            actual = TransformationInterceptor.SORT_WEIGHT
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val ele1 = TransformationInterceptor()
        val ele11 = TransformationInterceptor()

        assertEquals(ele1, ele11)
        assertNotEquals(ele1, Any())
        assertNotEquals(ele1, null as Any?)

        assertEquals(ele1.hashCode(), ele11.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "TransformationInterceptor",
            TransformationInterceptor().toString()
        )
    }
}