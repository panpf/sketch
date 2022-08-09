/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.fixedPrecision
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixedPrecisionDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(FixedPrecisionDecider(EXACTLY), fixedPrecision(EXACTLY))
        Assert.assertEquals(FixedPrecisionDecider(LESS_PIXELS), fixedPrecision(LESS_PIXELS))
        Assert.assertEquals(
            FixedPrecisionDecider(SAME_ASPECT_RATIO),
            fixedPrecision(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testGet() {
        FixedPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 50, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 51, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(100, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 50, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 51, 50, 50))
            Assert.assertEquals(EXACTLY, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(LESS_PIXELS).apply {
            Assert.assertEquals(LESS_PIXELS, get(100, 32, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(100, 33, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(100, 34, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "Fixed(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).key
        )
        Assert.assertEquals(
            "Fixed(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "Fixed(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).key
        )
    }

    @Test
    fun testToString() {
        Assert.assertEquals(
            "FixedPrecisionDecider(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "FixedPrecisionDecider(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).toString()
        )
    }
}