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

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.getCircleCropTransformed
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class CircleCropTransformationTest {

    @Test
    fun testConstructor() {
        CircleCropTransformation().apply {
            assertEquals(null, scale)
        }
        CircleCropTransformation(Scale.START_CROP).apply {
            assertEquals(Scale.START_CROP, scale)
        }
    }

    @Test
    fun testKeyAndToString() {
        CircleCropTransformation(Scale.CENTER_CROP).apply {
            assertEquals("CircleCropTransformation(CENTER_CROP)", key)
            assertEquals("CircleCropTransformation(CENTER_CROP)", toString())
        }
        CircleCropTransformation(Scale.START_CROP).apply {
            assertEquals("CircleCropTransformation(START_CROP)", key)
            assertEquals("CircleCropTransformation(START_CROP)", toString())
        }
    }

    @Test
    fun testTransform() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
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

        CircleCropTransformation(Scale.START_CROP)
            .transform(requestContext, inBitmap)!!.apply {
                assertNotSame(inBitmap, image)
                assertEquals(
                    listOf(
                        TestColor.TRANSPARENT,
                        TestColor.TRANSPARENT,
                        TestColor.TRANSPARENT,
                        TestColor.TRANSPARENT
                    ),
                    image.corners()
                )
                assertEquals(Size(1291, 1291), image.size)
                assertEquals(createCircleCropTransformed(Scale.START_CROP), transformed)
            }
    }

    @Test
    fun testEqualsAndHashCode() {
        val transformation1 = CircleCropTransformation(Scale.START_CROP)
        val transformation11 = CircleCropTransformation(Scale.START_CROP)
        val transformation2 = CircleCropTransformation(Scale.CENTER_CROP)
        val transformation3 = CircleCropTransformation(Scale.END_CROP)

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
    fun testCircleCropTransformed() {
        assertEquals(
            "CircleCropTransformed(START_CROP)",
            createCircleCropTransformed(Scale.START_CROP)
        )
        assertEquals(
            "CircleCropTransformed(CENTER_CROP)",
            createCircleCropTransformed(Scale.CENTER_CROP)
        )
        assertEquals(
            "CircleCropTransformed(END_CROP)",
            createCircleCropTransformed(Scale.END_CROP)
        )
        assertEquals("CircleCropTransformed(FILL)", createCircleCropTransformed(Scale.FILL))

        assertEquals(null, listOf<String>().getCircleCropTransformed())
        assertEquals(
            "CircleCropTransformed(CENTER_CROP)",
            listOf(createCircleCropTransformed(Scale.CENTER_CROP)).getCircleCropTransformed()
        )
        assertEquals(
            "CircleCropTransformed(FILL)",
            listOf(
                "disruptive1",
                createCircleCropTransformed(Scale.FILL),
                "disruptive2"
            ).getCircleCropTransformed()
        )
    }
}