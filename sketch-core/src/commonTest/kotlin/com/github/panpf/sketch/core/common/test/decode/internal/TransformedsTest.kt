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

import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.internal.createSubsamplingTransformed
import com.github.panpf.sketch.decode.internal.getInSampledTransformed
import com.github.panpf.sketch.decode.internal.getResizeTransformed
import com.github.panpf.sketch.decode.internal.getScaledTransformed
import com.github.panpf.sketch.decode.internal.getSubsamplingTransformed
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformedsTest {

    @Test
    fun testInSampledTransformed() {
        assertEquals(
            expected = "InSampledTransformed(1)",
            actual = createInSampledTransformed(1)
        )
        assertEquals(
            expected = "InSampledTransformed(2)",
            actual = createInSampledTransformed(2)
        )
        assertEquals(
            expected = "InSampledTransformed(4)",
            actual = createInSampledTransformed(4)
        )
        assertEquals(
            expected = "InSampledTransformed(8)",
            actual = createInSampledTransformed(8)
        )

        assertEquals(
            expected = null,
            actual = listOf<String>().getInSampledTransformed()
        )
        assertEquals(
            expected = "InSampledTransformed(2)",
            actual = listOf(createInSampledTransformed(2)).getInSampledTransformed()
        )
        assertEquals(
            expected = "InSampledTransformed(16)",
            actual = listOf(
                "disruptive1",
                createInSampledTransformed(16),
                "disruptive2"
            ).getInSampledTransformed()
        )
    }

    @Test
    fun testSubsamplingTransformed() {
        assertEquals(
            expected = "SubsamplingTransformed(1,2,3,4)",
            actual = createSubsamplingTransformed(Rect(1, 2, 3, 4))
        )
        assertEquals(
            expected = "SubsamplingTransformed(2,3,4,5)",
            actual = createSubsamplingTransformed(Rect(2, 3, 4, 5))
        )
        assertEquals(
            expected = "SubsamplingTransformed(3,4,5,6)",
            actual = createSubsamplingTransformed(Rect(3, 4, 5, 6))
        )
        assertEquals(
            expected = "SubsamplingTransformed(4,5,6,7)",
            actual = createSubsamplingTransformed(Rect(4, 5, 6, 7))
        )

        assertEquals(
            expected = null,
            actual = listOf<String>().getSubsamplingTransformed()
        )
        assertEquals(
            expected = "SubsamplingTransformed(1,2,3,4)",
            actual = listOf(
                createSubsamplingTransformed(
                    Rect(
                        1,
                        2,
                        3,
                        4
                    )
                )
            ).getSubsamplingTransformed()
        )
        assertEquals(
            expected = "SubsamplingTransformed(1,2,3,4)",
            actual = listOf(
                "disruptive1",
                createSubsamplingTransformed(Rect(1, 2, 3, 4)),
                "disruptive2"
            ).getSubsamplingTransformed()
        )
    }

    @Test
    fun testResizeTransformed() {
        assertEquals(
            expected = "ResizeTransformed(100x100,LESS_PIXELS,CENTER_CROP)",
            actual = createResizeTransformed(Resize(100, 100))
        )
        assertEquals(
            expected = "ResizeTransformed(200x200,LESS_PIXELS,CENTER_CROP)",
            actual = createResizeTransformed(Resize(200, 200))
        )
        assertEquals(
            expected = "ResizeTransformed(300x300,LESS_PIXELS,CENTER_CROP)",
            actual = createResizeTransformed(Resize(300, 300))
        )
        assertEquals(
            expected = "ResizeTransformed(400x400,LESS_PIXELS,CENTER_CROP)",
            actual = createResizeTransformed(Resize(400, 400))
        )

        assertEquals(
            expected = null,
            actual = listOf<String>().getResizeTransformed()
        )
        assertEquals(
            expected = "ResizeTransformed(200x200,LESS_PIXELS,CENTER_CROP)",
            actual = listOf(createResizeTransformed(Resize(200, 200))).getResizeTransformed()
        )
        assertEquals(
            expected = "ResizeTransformed(500x500,LESS_PIXELS,CENTER_CROP)",
            actual = listOf(
                "disruptive1",
                createResizeTransformed(Resize(500, 500)),
                "disruptive2"
            ).getResizeTransformed()
        )
    }

    @Test
    fun testScaledTransformed() {
        assertEquals(
            expected = "ScaledTransformed(1.0)",
            actual = createScaledTransformed(1f)
        )
        assertEquals(
            expected = "ScaledTransformed(2.0)",
            actual = createScaledTransformed(2f)
        )
        assertEquals(
            expected = "ScaledTransformed(4.0)",
            actual = createScaledTransformed(4f)
        )
        assertEquals(
            expected = "ScaledTransformed(8.0)",
            actual = createScaledTransformed(8f)
        )

        assertEquals(
            expected = null,
            actual = listOf<String>().getScaledTransformed()
        )
        assertEquals(
            expected = "ScaledTransformed(2.0)",
            actual = listOf(createScaledTransformed(2f)).getScaledTransformed()
        )
        assertEquals(
            expected = "ScaledTransformed(16.0)",
            actual = listOf(
                "disruptive1",
                createScaledTransformed(16f),
                "disruptive2"
            ).getScaledTransformed()
        )
    }
}