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

package com.github.panpf.sketch.core.android.test.resize.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import com.github.panpf.sketch.util.Rect
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeMappingTest {

    @Test
    fun testCalculatorResizeMappingSame() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 200), Rect(0, 0, 200, 200)),
            calculateResizeMapping(200, 200, 200, 200, LESS_PIXELS, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingLessPixels() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 20, 40, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 40, 20, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 14, 56)),
            calculateResizeMapping(50, 200, 20, 40, LESS_PIXELS, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 100, 150, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 200, 50)),
            calculateResizeMapping(200, 50, 150, 100, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 100, 150, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 50, 200)),
            calculateResizeMapping(50, 200, 150, 100, LESS_PIXELS, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingSmallerSize() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 10)),
            calculateResizeMapping(200, 50, 40, 20, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 20, 5)),
            calculateResizeMapping(200, 50, 20, 40, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 5, 20)),
            calculateResizeMapping(50, 200, 40, 20, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 10, 40)),
            calculateResizeMapping(50, 200, 20, 40, SMALLER_SIZE, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 100, 25)),
            calculateResizeMapping(200, 50, 100, 150, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 150, 37)),
            calculateResizeMapping(200, 50, 150, 100, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 37, 150)),
            calculateResizeMapping(50, 200, 100, 150, SMALLER_SIZE, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 200), Rect(0, 0, 25, 100)),
            calculateResizeMapping(50, 200, 150, 100, SMALLER_SIZE, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingKeepAspectRatio() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 25, 50), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 25), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 100), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, SAME_ASPECT_RATIO, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 33, 50)),
            calculateResizeMapping(200, 50, 100, 150, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 75, 50)),
            calculateResizeMapping(200, 50, 150, 100, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 50, 75)),
            calculateResizeMapping(50, 200, 100, 150, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 50, 33)),
            calculateResizeMapping(50, 200, 150, 100, SAME_ASPECT_RATIO, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingExactly() {
        /* resize < imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 25, 50), Rect(0, 0, 20, 40)),
            calculateResizeMapping(200, 50, 20, 40, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 25), Rect(0, 0, 40, 20)),
            calculateResizeMapping(50, 200, 40, 20, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 100), Rect(0, 0, 20, 40)),
            calculateResizeMapping(50, 200, 20, 40, EXACTLY, START_CROP)
        )

        /* resize > imageSize */
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 33, 50), Rect(0, 0, 100, 150)),
            calculateResizeMapping(200, 50, 100, 150, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 75, 50), Rect(0, 0, 150, 100)),
            calculateResizeMapping(200, 50, 150, 100, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 75), Rect(0, 0, 100, 150)),
            calculateResizeMapping(50, 200, 100, 150, EXACTLY, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 50, 33), Rect(0, 0, 150, 100)),
            calculateResizeMapping(50, 200, 150, 100, EXACTLY, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingStartCrop() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, START_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 100, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, START_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingCenterCrop() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, CENTER_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(50, 0, 150, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, CENTER_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(50, 0, 150, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, CENTER_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingEndCrop() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, END_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(100, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, END_CROP)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(100, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, END_CROP)
        )
    }

    @Test
    fun testCalculatorResizeMappingFill() {
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 56, 14)),
            calculateResizeMapping(200, 50, 40, 20, LESS_PIXELS, FILL)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, SAME_ASPECT_RATIO, FILL)
        )
        Assert.assertEquals(
            ResizeMapping(Rect(0, 0, 200, 50), Rect(0, 0, 40, 20)),
            calculateResizeMapping(200, 50, 40, 20, EXACTLY, FILL)
        )
    }
}