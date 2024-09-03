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

package com.github.panpf.sketch.animated.android.test.transform

import android.graphics.Canvas
import android.graphics.PaintFlagsDrawFilter
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.transform.PixelOpacity.TRANSLUCENT
import com.github.panpf.sketch.transform.asPostProcessor
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class AnimatedTransformationTest {

    @Test
    fun testAsPostProcessor() {
        if (Build.VERSION.SDK_INT < 28) {
            return
        }

        val drawFilter1 = PaintFlagsDrawFilter(10, 8)
        val transformation = DrawFilterAnimatedTransformation(drawFilter1)
        val canvas = Canvas()
        assertNull(canvas.drawFilter)

        val processor = transformation.asPostProcessor()
        processor.onPostProcess(canvas)
        assertEquals(drawFilter1, canvas.drawFilter)
    }

    data class DrawFilterAnimatedTransformation(val drawFilter: PaintFlagsDrawFilter) :
        AnimatedTransformation {
        override val key: String = "DrawFilterAnimatedTransformation"

        override fun transform(canvas: Canvas): PixelOpacity {
            canvas.drawFilter = drawFilter
            return TRANSLUCENT
        }
    }
}