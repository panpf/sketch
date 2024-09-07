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

package com.github.panpf.sketch.core.common.test.transform

import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.createMaskTransformed
import com.github.panpf.sketch.transform.getMaskTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class MaskTransformationTest {

    @Test
    fun testConstructor() {
        MaskTransformation(TestColor.BLACK).apply {
            assertEquals(TestColor.BLACK, maskColor)
        }
        MaskTransformation(TestColor.GREEN).apply {
            assertEquals(TestColor.GREEN, maskColor)
        }
    }

    @Test
    fun testKeyAndToString() {
        MaskTransformation(TestColor.BLACK).apply {
            assertEquals("MaskTransformation(${TestColor.BLACK})", key)
            assertEquals("MaskTransformation(${TestColor.BLACK})", toString())
        }
        MaskTransformation(TestColor.GREEN).apply {
            assertEquals("MaskTransformation(${TestColor.GREEN})", key)
            assertEquals("MaskTransformation(${TestColor.GREEN})", toString())
        }
    }

    @Test
    fun testTransform() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(Size.Origin)
        }
        val requestContext = request.toRequestContext(sketch)

        val inBitmap = request.decode(sketch).image.apply {
            assertNotEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                this.corners()
            )
            assertEquals(
                Size(1291, 1936),
                this.size
            )
        }
        val inBitmapCorners = inBitmap.corners()

        val maskColor = TestColor.withA(TestColor.GREEN, 100)
        MaskTransformation(maskColor).transform(requestContext, inBitmap).apply {
            assertNotSame(inBitmap, this.image)
            assertNotEquals(inBitmapCorners, image.corners())
            assertEquals(Size(1291, 1936), image.size)
            assertEquals(createMaskTransformed(maskColor), transformed)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = MaskTransformation(TestColor.RED)
        val element11 = MaskTransformation(TestColor.RED)
        val element2 = MaskTransformation(TestColor.BLACK)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testMaskTransformed() {
        assertEquals("MaskTransformed(1)", createMaskTransformed(1))
        assertEquals("MaskTransformed(2)", createMaskTransformed(2))
        assertEquals("MaskTransformed(4)", createMaskTransformed(4))
        assertEquals("MaskTransformed(8)", createMaskTransformed(8))

        assertEquals(null, listOf<String>().getMaskTransformed())
        assertEquals(
            "MaskTransformed(2)",
            listOf(createMaskTransformed(2)).getMaskTransformed()
        )
        assertEquals(
            "MaskTransformed(16)",
            listOf(
                "disruptive1",
                createMaskTransformed(16),
                "disruptive2"
            ).getMaskTransformed()
        )
    }
}