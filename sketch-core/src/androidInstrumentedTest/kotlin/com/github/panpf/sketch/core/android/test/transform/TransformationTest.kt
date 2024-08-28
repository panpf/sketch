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

package com.github.panpf.sketch.core.android.test.transform

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.merge
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformationTest {

    @Test
    fun testMerge() {
        val list1 = listOf(CircleCropTransformation(), RoundedCornersTransformation())
        val list2 = listOf(RoundedCornersTransformation(), RotateTransformation(45))
        val nullElement = null as List<Transformation>?

        list1.merge(list1)!!.apply {
            Assert.assertEquals(list1, this)
            Assert.assertNotSame(list1, this)
        }
        list2.merge(list2)!!.apply {
            Assert.assertEquals(list2, this)
            Assert.assertNotSame(list2, this)
        }
        list1.merge(list2)!!.apply {
            Assert.assertEquals(
                listOf(
                    CircleCropTransformation(),
                    RoundedCornersTransformation(),
                    RotateTransformation(45)
                ),
                this
            )
            Assert.assertSame(list1[1], this[1])
            Assert.assertEquals(list2[0], this[1])
            Assert.assertNotSame(list2[0], this[1])
        }

        Assert.assertSame(list2, nullElement.merge(list2))
        Assert.assertSame(list1, list1.merge(nullElement))
    }
}