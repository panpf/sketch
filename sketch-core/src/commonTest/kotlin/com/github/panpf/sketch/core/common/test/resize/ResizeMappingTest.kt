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

import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ResizeMappingTest {

    @Test
    fun testNewSize() {
        val resizeMapping = ResizeMapping(
            srcRect = Rect(0, 0, 100, 100),
            dstRect = Rect(0, 0, 50, 50)
        )
        assertEquals(Size(50, 50), resizeMapping.newSize)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResizeMapping(
            srcRect = Rect(0, 0, 100, 100),
            dstRect = Rect(0, 0, 50, 50)
        )
        val element11 = element1.copy()
        val element2 = element1.copy(srcRect = Rect(0, 0, 200, 200))
        val element3 = element1.copy(dstRect = Rect(0, 0, 300, 300))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ResizeMapping(srcRect=Rect(0, 0 - 100, 100), dstRect=Rect(0, 0 - 50, 50))",
            actual = ResizeMapping(
                srcRect = Rect(0, 0, 100, 100),
                dstRect = Rect(0, 0, 50, 50)
            ).toString()
        )
    }
}