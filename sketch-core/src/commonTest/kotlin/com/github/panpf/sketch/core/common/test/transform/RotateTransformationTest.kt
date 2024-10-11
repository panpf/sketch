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
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.createRotateTransformed
import com.github.panpf.sketch.transform.getRotateTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class RotateTransformationTest {

    @Test
    fun testConstructor() {
        RotateTransformation(12).apply {
            assertEquals(12, degrees)
        }
        RotateTransformation(20).apply {
            assertEquals(20, degrees)
        }
    }

    @Test
    fun testKeyAndToString() {
        RotateTransformation(12).apply {
            assertEquals("RotateTransformation(12)", key)
            assertEquals("RotateTransformation(12)", toString())
        }
        RotateTransformation(20).apply {
            assertEquals("RotateTransformation(20)", key)
            assertEquals("RotateTransformation(20)", toString())
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

        RotateTransformation(90).transform(requestContext, inBitmap)!!.apply {
            assertNotSame(inBitmap, this.image)
            assertEquals(
                listOf(
                    inBitmapCorners[3],
                    inBitmapCorners[0],
                    inBitmapCorners[1],
                    inBitmapCorners[2],
                ),
                image.corners()
            )
            assertEquals(Size(1936, 1291), image.size)
            assertEquals(
                createRotateTransformed(RotateTransformation(90).degrees),
                transformed
            )
        }

        RotateTransformation(450).transform(requestContext, inBitmap)!!.apply {
            assertNotSame(inBitmap, this.image)
            assertEquals(
                listOf(
                    inBitmapCorners[3],
                    inBitmapCorners[0],
                    inBitmapCorners[1],
                    inBitmapCorners[2],
                ),
                image.corners()
            )
            assertEquals(Size(1936, 1291), image.size)
            assertEquals(
                createRotateTransformed(RotateTransformation(450).degrees),
                transformed
            )
        }

        RotateTransformation(45).transform(requestContext, inBitmap)!!.apply {
            assertNotSame(inBitmap, this.image)
            assertEquals(
                listOf(
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT,
                    TestColor.TRANSPARENT
                ),
                image.corners()
            )
            assertEquals(Size(2281, 2281), image.size)
            assertEquals(
                createRotateTransformed(RotateTransformation(45).degrees),
                transformed
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val transformation1 = RotateTransformation(12)
        val transformation11 = RotateTransformation(12)
        val transformation2 = RotateTransformation(22)
        val transformation3 = RotateTransformation(32)

        assertEquals(transformation1, transformation11)
        assertNotEquals(transformation1, transformation2)
        assertNotEquals(transformation1, transformation3)
        assertNotEquals(transformation2, transformation3)
        assertNotEquals(transformation2, null as Any?)
        assertNotEquals(transformation2, Any())

        assertEquals(transformation1.hashCode(), transformation11.hashCode())
        assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }

    @Test
    fun testRotateTransformed() {
        assertEquals("RotateTransformed(1)", createRotateTransformed(1))
        assertEquals("RotateTransformed(2)", createRotateTransformed(2))
        assertEquals("RotateTransformed(4)", createRotateTransformed(4))
        assertEquals("RotateTransformed(8)", createRotateTransformed(8))

        assertEquals(null, listOf<String>().getRotateTransformed())
        assertEquals(
            "RotateTransformed(2)",
            listOf(createRotateTransformed(2)).getRotateTransformed()
        )
        assertEquals(
            "RotateTransformed(16)",
            listOf(
                "disruptive1",
                createRotateTransformed(16),
                "disruptive2"
            ).getRotateTransformed()
        )
    }
}