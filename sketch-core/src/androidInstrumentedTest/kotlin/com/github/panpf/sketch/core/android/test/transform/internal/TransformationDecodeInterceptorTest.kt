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
package com.github.panpf.sketch.core.android.test.transform.internal

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.DecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.TransformResult
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.internal.TransformationDecodeInterceptor
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformationDecodeInterceptorTest {

    @Test
    fun testIntercept() {
        val (context, sketch) = getTestContextAndSketch()
        val interceptors =
            listOf(EngineDecodeInterceptor())

        runBlocking {
            val request = ImageRequest(context, MyImages.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
            }
            val chain = DecodeInterceptorChain(
                sketch,
                request,
                request.toRequestContext(sketch),
                null,
                interceptors,
                0
            )
            TransformationDecodeInterceptor().intercept(chain)
        }.getOrThrow().apply {
            Assert.assertEquals(Size(1291, 1936), image.getBitmapOrThrow().size)
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                image.getBitmapOrThrow().corners()
            )
            Assert.assertNull(transformedList)
        }

        runBlocking {
            val request = ImageRequest(context, MyImages.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(CircleCropTransformation())
            }
            val chain = DecodeInterceptorChain(
                sketch,
                request,
                request.toRequestContext(sketch),
                null,
                interceptors,
                0
            )
            TransformationDecodeInterceptor().intercept(chain)
        }.getOrThrow().apply {
            Assert.assertEquals(Size(1291, 1291), image.getBitmapOrThrow().size)
            Assert.assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                image.getBitmapOrThrow().corners()
            )
            Assert.assertEquals(listOf(createCircleCropTransformed(CENTER_CROP)), transformedList)
        }

        runBlocking {
            val request = ImageRequest(context, MyImages.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(object : Transformation {
                    override val key: String
                        get() = "TestTransformation"

                    override suspend fun transform(
                        sketch: Sketch,
                        requestContext: RequestContext,
                        input: Image
                    ): TransformResult = TransformResult(input, "TestTransformation")
                })
            }
            val chain = DecodeInterceptorChain(
                sketch,
                request,
                request.toRequestContext(sketch),
                null,
                interceptors,
                0
            )
            TransformationDecodeInterceptor().intercept(chain)
        }.getOrThrow().apply {
            Assert.assertEquals(Size(1291, 1936), image.getBitmapOrThrow().size)
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                image.getBitmapOrThrow().corners()
            )
            Assert.assertNotNull(transformedList)
        }

        runBlocking {
            val request = ImageRequest(context, MyImages.jpeg.uri) {
                size(3000, 3000)
                precision(LESS_PIXELS)
                transformations(object : Transformation {
                    override val key: String
                        get() = "TestTransformation"

                    override suspend fun transform(
                        sketch: Sketch,
                        requestContext: RequestContext,
                        input: Image
                    ): TransformResult? = null
                })
            }
            val chain = DecodeInterceptorChain(
                sketch,
                request,
                request.toRequestContext(sketch),
                null,
                interceptors,
                0
            )
            TransformationDecodeInterceptor().intercept(chain)
        }.getOrThrow().apply {
            Assert.assertEquals(Size(1291, 1936), image.getBitmapOrThrow().size)
            Assert.assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                image.getBitmapOrThrow().corners()
            )
            Assert.assertNull(transformedList)
        }
    }

    @Test
    fun testSortWeight() {
        TransformationDecodeInterceptor().apply {
            Assert.assertEquals(90, sortWeight)
        }
    }

    @Test
    fun testEquals() {
        val ele1 = TransformationDecodeInterceptor()
        val ele2 = TransformationDecodeInterceptor()
        Assert.assertEquals(ele1, ele1)
        Assert.assertEquals(ele1, ele2)
        Assert.assertNotEquals(ele1, Any())
        Assert.assertNotEquals(ele1, null)
    }

    @Test
    fun testHashCode() {
        val ele1 = TransformationDecodeInterceptor()
        val ele2 = TransformationDecodeInterceptor()
        Assert.assertEquals(ele1.hashCode(), ele2.hashCode())
        Assert.assertNotEquals(ele1.hashCode(), Any().hashCode())
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "TransformationDecodeInterceptor(sortWeight=90)",
            TransformationDecodeInterceptor().toString()
        )
    }
}