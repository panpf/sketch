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
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.runBlock
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.createRoundedCornersTransformed
import com.github.panpf.sketch.transform.getRoundedCornersTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class RoundedCornersTransformationTest {

    @Test
    fun testConstructor() {
        assertFailsWith(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f))
        }
        assertFailsWith(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f))
        }
        assertFailsWith(IllegalArgumentException::class) {
            RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, -4f, 5f, 6f, 7f, 8f))
        }

        RoundedCornersTransformation(floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f)).apply {
            assertEquals(
                floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f).toList(),
                radiusArray.toList()
            )
        }

        RoundedCornersTransformation(
            topLeft = 1f,
            topRight = 2f,
            bottomLeft = 3f,
            bottomRight = 4f
        ).apply {
            assertEquals(
                floatArrayOf(1f, 1f, 2f, 2f, 4f, 4f, 3f, 3f).toList(),
                radiusArray.toList()
            )
        }

        RoundedCornersTransformation(
            allRadius = 1f,
        ).apply {
            assertEquals(
                floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f).toList(),
                radiusArray.toList()
            )
        }
    }

    @Test
    fun testKeyAndToString() {
        RoundedCornersTransformation(1f).apply {
            assertEquals(
                "RoundedCornersTransformation(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0)",
                key
            )
            assertEquals(
                "RoundedCornersTransformation(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0)",
                toString()
            )
        }
        RoundedCornersTransformation(2f).apply {
            assertEquals(
                "RoundedCornersTransformation(2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0)",
                key
            )
            assertEquals(
                "RoundedCornersTransformation(2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0)",
                toString()
            )
        }
    }

    @Test
    fun testTransform() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri) {
            size(Size.Origin)
        }

        val inBitmap = request.decode(sketch).image.apply {
            assertNotEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                this.corners()
            )
            assertEquals(
                Size(1291, 1936),
                this.size
            )
        }

        runBlock {
            RoundedCornersTransformation(20f).transform(
                sketch,
                request.toRequestContext(sketch),
                inBitmap
            )
        }.apply {
            assertNotSame(inBitmap, image)
            assertEquals(
                listOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT),
                image.corners()
            )
            assertEquals(Size(1291, 1936), image.size)
            assertEquals(
                createRoundedCornersTransformed(RoundedCornersTransformation(20f).radiusArray),
                transformed
            )
        }
    }

    @Test
    fun testEquals() {
        val transformation1 = RoundedCornersTransformation(10f)
        val transformation11 = RoundedCornersTransformation(10f)

        val transformation2 = RoundedCornersTransformation(20f)
        val transformation21 = RoundedCornersTransformation(20f)

        val transformation3 = RoundedCornersTransformation(30f)
        val transformation31 = RoundedCornersTransformation(30f)

        assertNotSame(transformation1, transformation11)
        assertNotSame(transformation2, transformation21)
        assertNotSame(transformation3, transformation31)

        assertEquals(transformation1, transformation1)
        assertEquals(transformation1, transformation11)
        assertEquals(transformation2, transformation21)
        assertEquals(transformation3, transformation31)

        assertNotEquals(transformation1, transformation2)
        assertNotEquals(transformation1, transformation3)
        assertNotEquals(transformation2, transformation3)

        assertNotEquals(transformation2, null as Any?)
        assertNotEquals(transformation2, Any())
    }

    @Test
    fun testHashCode() {
        val transformation1 = RoundedCornersTransformation(10f)
        val transformation11 = RoundedCornersTransformation(10f)

        val transformation2 = RoundedCornersTransformation(20f)
        val transformation21 = RoundedCornersTransformation(20f)

        val transformation3 = RoundedCornersTransformation(30f)
        val transformation31 = RoundedCornersTransformation(30f)

        assertEquals(transformation1.hashCode(), transformation11.hashCode())
        assertEquals(transformation2.hashCode(), transformation21.hashCode())
        assertEquals(transformation3.hashCode(), transformation31.hashCode())

        assertNotEquals(transformation1.hashCode(), transformation2.hashCode())
        assertNotEquals(transformation1.hashCode(), transformation3.hashCode())
        assertNotEquals(transformation2.hashCode(), transformation3.hashCode())
    }

    @Test
    fun testRoundedCornersTransformed() {
        val buildRadiusArray: (radius: Int) -> FloatArray = {
            floatArrayOf(
                it.toFloat(), it.toFloat(),
                it.toFloat(), it.toFloat(),
                it.toFloat(), it.toFloat(),
                it.toFloat(), it.toFloat()
            )
        }

        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(1).contentToString()})",
            createRoundedCornersTransformed(buildRadiusArray(1))
        )
        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(2).contentToString()})",
            createRoundedCornersTransformed(buildRadiusArray(2))
        )
        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(4).contentToString()})",
            createRoundedCornersTransformed(buildRadiusArray(4))
        )
        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(8).contentToString()})",
            createRoundedCornersTransformed(buildRadiusArray(8))
        )

        assertEquals(null, listOf<String>().getRoundedCornersTransformed())
        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(2).contentToString()})",
            listOf(createRoundedCornersTransformed(buildRadiusArray(2))).getRoundedCornersTransformed()
        )
        assertEquals(
            "RoundedCornersTransformed(${buildRadiusArray(16).contentToString()})",
            listOf(
                "disruptive1",
                createRoundedCornersTransformed(buildRadiusArray(16)),
                "disruptive2"
            ).getRoundedCornersTransformed()
        )
    }
}