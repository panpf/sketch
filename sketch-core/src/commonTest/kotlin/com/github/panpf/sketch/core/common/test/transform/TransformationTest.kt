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

import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.merge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class TransformationTest {

    @Test
    fun testMerge() {
        val list1 = listOf(CircleCropTransformation(), RoundedCornersTransformation())
        val list2 = listOf(RoundedCornersTransformation(), RotateTransformation(45))
        val nullElement = null as List<Transformation>?

        list1.merge(list1)!!.apply {
            assertEquals(list1, this)
            assertNotSame(list1, this)
        }
        list2.merge(list2)!!.apply {
            assertEquals(list2, this)
            assertNotSame(list2, this)
        }
        list1.merge(list2)!!.apply {
            assertEquals(
                listOf(
                    CircleCropTransformation(),
                    RoundedCornersTransformation(),
                    RotateTransformation(45)
                ),
                this
            )
            assertSame(list1[1], this[1])
            assertEquals(list2[0], this[1])
            assertNotSame(list2[0], this[1])
        }

        assertSame(list2, nullElement.merge(list2))
        assertSame(list1, list1.merge(nullElement))
    }
}