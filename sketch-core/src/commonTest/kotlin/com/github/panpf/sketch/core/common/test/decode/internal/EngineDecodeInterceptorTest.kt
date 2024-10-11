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

package com.github.panpf.sketch.core.common.test.decode.internal

import com.github.panpf.sketch.decode.internal.DecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.EngineDecodeInterceptor
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class EngineDecodeInterceptorTest {

    @Test
    fun testIntercept() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val interceptors = listOf(EngineDecodeInterceptor())
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }
        val chain = DecodeInterceptorChain(
            requestContext = request.toRequestContext(sketch),
            fetchResult = null,
            interceptors = interceptors,
            index = 0
        )
        val result = chain.proceed().getOrThrow()
        assertEquals(1291, result.image.width)
        assertEquals(1936, result.image.height)
        assertEquals(
            "ImageInfo(size=1291x1936, mimeType='image/jpeg')",
            result.imageInfo.toString()
        )
        assertEquals(DataFrom.LOCAL, result.dataFrom)
        assertNull(result.transformeds)
    }

    @Test
    fun testSortWeight() {
        EngineDecodeInterceptor().apply {
            assertEquals(100, sortWeight)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = EngineDecodeInterceptor()
        val element11 = EngineDecodeInterceptor()
        val element2 = EngineDecodeInterceptor()

        assertEquals(element1, element11)
        assertEquals(element1, element2)
        assertEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            "EngineDecodeInterceptor(sortWeight=100)",
            EngineDecodeInterceptor().toString()
        )
    }
}