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

package com.github.panpf.sketch.core.common.test.resize

import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResizeTest {

    @Test
    fun testConstructor() {
        Resize(100, 30, SAME_ASPECT_RATIO, END_CROP).apply {
            assertEquals(100, size.width)
            assertEquals(30, size.height)
            assertEquals(SAME_ASPECT_RATIO, precision)
            assertEquals(END_CROP, scale)
        }

        Resize(100, 30).apply {
            assertEquals(100, size.width)
            assertEquals(30, size.height)
            assertEquals(LESS_PIXELS, precision)
            assertEquals(CENTER_CROP, scale)
        }

        Resize(100, 30, SAME_ASPECT_RATIO).apply {
            assertEquals(100, size.width)
            assertEquals(30, size.height)
            assertEquals(SAME_ASPECT_RATIO, precision)
            assertEquals(CENTER_CROP, scale)
        }

        Resize(100, 30, END_CROP).apply {
            assertEquals(100, size.width)
            assertEquals(30, size.height)
            assertEquals(LESS_PIXELS, precision)
            assertEquals(END_CROP, scale)
        }
    }

    @Test
    fun testKey() {
        Resize(100, 100, LESS_PIXELS, CENTER_CROP).apply {
            assertEquals("Resize(100x100,LESS_PIXELS,CENTER_CROP)", key)
        }
        Resize(414, 786, SAME_ASPECT_RATIO, END_CROP).apply {
            assertEquals("Resize(414x786,SAME_ASPECT_RATIO,END_CROP)", key)
        }
    }

    @Test
    fun testToString() {
        Resize(100, 100, LESS_PIXELS, CENTER_CROP).apply {
            assertEquals(
                "Resize(size=100x100, precision=LESS_PIXELS, scale=CENTER_CROP)",
                toString()
            )
        }
        Resize(414, 786, SAME_ASPECT_RATIO, END_CROP).apply {
            assertEquals(
                "Resize(size=414x786, precision=SAME_ASPECT_RATIO, scale=END_CROP)",
                toString()
            )
        }
    }

    @Test
    fun testShouldClip() {
        Resize(100, 100, LESS_PIXELS).apply {
            assertFalse(shouldClip(100, 50))
            assertFalse(shouldClip(100, 150))
            assertFalse(shouldClip(50, 100))
            assertFalse(shouldClip(150, 100))
            assertFalse(shouldClip(100, 100))
            assertFalse(shouldClip(50, 50))
            assertFalse(shouldClip(150, 150))
        }

        Resize(100, 100, SAME_ASPECT_RATIO).apply {
            assertTrue(shouldClip(100, 50))
            assertTrue(shouldClip(100, 150))
            assertTrue(shouldClip(50, 100))
            assertTrue(shouldClip(150, 100))
            assertFalse(shouldClip(100, 100))
            assertFalse(shouldClip(50, 50))
            assertFalse(shouldClip(150, 150))
        }

        Resize(100, 100, EXACTLY).apply {
            assertTrue(shouldClip(100, 50))
            assertTrue(shouldClip(100, 150))
            assertTrue(shouldClip(50, 100))
            assertTrue(shouldClip(150, 100))
            assertFalse(shouldClip(100, 100))
            assertTrue(shouldClip(50, 50))
            assertTrue(shouldClip(150, 150))
        }
    }

    @Test
    fun testCalculateMappingSame() {
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 200), Rect(0, 0, 200, 200)),
            calculateResizeMapping(200, 200, 200, 200, LESS_PIXELS, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingLessPixels() {
        /* resize < imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 20, 40, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 40, 20, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 20, 40, LESS_PIXELS, START_CROP)
        )

        /* resize > imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 100, 150, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 150, 100, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 100, 150, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 150, 100, LESS_PIXELS, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingSmallerSize() {
        /* resize < imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 10)),
            calculateResizeMapping(200, 50, 40, 20, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 20, 5)),
            calculateResizeMapping(200, 50, 20, 40, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 5, 20)),
            calculateResizeMapping(50, 200, 40, 20, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 10, 40)),
            calculateResizeMapping(50, 200, 20, 40, SMALLER_SIZE, START_CROP)
        )

        /* resize > imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 100, 25)),
            calculateResizeMapping(200, 50, 100, 150, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 150, 37)),
            calculateResizeMapping(200, 50, 150, 100, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 37, 150)),
            calculateResizeMapping(50, 200, 100, 150, SMALLER_SIZE, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 25, 100)),
            calculateResizeMapping(50, 200, 150, 100, SMALLER_SIZE, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingKeepAspectRatio() {
        /* resize < imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 25, 50), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 25), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 100), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, SAME_ASPECT_RATIO, START_CROP)
        )

        /* resize > imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 33, 50)),
            calculateResizeMapping(200, 50, 100, 150, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 75, 50)),
            calculateResizeMapping(200, 50, 150, 100, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 50, 75)),
            calculateResizeMapping(50, 200, 100, 150, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 50, 33)),
            calculateResizeMapping(50, 200, 150, 100, SAME_ASPECT_RATIO, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingExactly() {
        /* resize < imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 25, 50), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 25), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 100), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, EXACTLY, START_CROP)
        )

        /* resize > imageSize */
        assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 100, 150)),
            calculateResizeMapping(200, 50, 100, 150, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 150, 100)),
            calculateResizeMapping(200, 50, 150, 100, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 100, 150)),
            calculateResizeMapping(50, 200, 100, 150, EXACTLY, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 150, 100)),
            calculateResizeMapping(50, 200, 150, 100, EXACTLY, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingStartCrop() {
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, START_CROP)
        )
    }

    @Test
    fun testCalculateMappingCenterCrop() {
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, CENTER_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(50, 0, 150, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, CENTER_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(50, 0, 150, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, CENTER_CROP)
        )
    }

    @Test
    fun testCalculateMappingEndCrop() {
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, END_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(100, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, END_CROP)
        )
        assertEquals(
            ResizeMapping(Rect(100, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, END_CROP)
        )
    }

    @Test
    fun testCalculateMappingFill() {
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, FILL)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, FILL)
        )
        assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, FILL)
        )
    }

    private fun calculateResizeMapping(
        imageWidth: Int,
        imageHeight: Int,
        resizeWidth: Int,
        resizeHeight: Int,
        precision: Precision,
        scale: Scale,
    ): ResizeMapping = Resize(
        size = Size(width = resizeWidth, height = resizeHeight),
        precision = precision,
        scale = scale
    ).calculateMapping(
        imageSize = Size(width = imageWidth, height = imageHeight),
    )

    private fun Resize.shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        shouldClip(Size(imageWidth, imageHeight))
}