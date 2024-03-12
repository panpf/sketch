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
package com.github.panpf.sketch.core.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeTest {

    @Test
    fun testConstructor() {
        Resize(100, 30, SAME_ASPECT_RATIO, END_CROP).apply {
            Assert.assertEquals(100, size.width)
            Assert.assertEquals(30, size.height)
            Assert.assertEquals(SAME_ASPECT_RATIO, precision)
            Assert.assertEquals(END_CROP, scale)
        }

        Resize(100, 30).apply {
            Assert.assertEquals(100, size.width)
            Assert.assertEquals(30, size.height)
            Assert.assertEquals(LESS_PIXELS, precision)
            Assert.assertEquals(CENTER_CROP, scale)
        }

        Resize(100, 30, SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(100, size.width)
            Assert.assertEquals(30, size.height)
            Assert.assertEquals(SAME_ASPECT_RATIO, precision)
            Assert.assertEquals(CENTER_CROP, scale)
        }

        Resize(100, 30, END_CROP).apply {
            Assert.assertEquals(100, size.width)
            Assert.assertEquals(30, size.height)
            Assert.assertEquals(LESS_PIXELS, precision)
            Assert.assertEquals(END_CROP, scale)
        }
    }

    @Test
    fun testKey() {
        Resize(100, 100, LESS_PIXELS, CENTER_CROP).apply {
            Assert.assertEquals("Resize(100x100,LESS_PIXELS,CENTER_CROP)", key)
        }
        Resize(414, 786, SAME_ASPECT_RATIO, END_CROP).apply {
            Assert.assertEquals("Resize(414x786,SAME_ASPECT_RATIO,END_CROP)", key)
        }
    }

    @Test
    fun testToString() {
        Resize(100, 100, LESS_PIXELS, CENTER_CROP).apply {
            Assert.assertEquals(
                "Resize(width=100, height=100, precision=LESS_PIXELS, scale=CENTER_CROP)",
                toString()
            )
        }
        Resize(414, 786, SAME_ASPECT_RATIO, END_CROP).apply {
            Assert.assertEquals(
                "Resize(width=414, height=786, precision=SAME_ASPECT_RATIO, scale=END_CROP)",
                toString()
            )
        }
    }

    @Test
    fun testShouldClip() {
        Resize(100, 100, LESS_PIXELS).apply {
            Assert.assertFalse(shouldClip(100, 50))
            Assert.assertFalse(shouldClip(100, 150))
            Assert.assertFalse(shouldClip(50, 100))
            Assert.assertFalse(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertFalse(shouldClip(50, 50))
            Assert.assertFalse(shouldClip(150, 150))
        }

        Resize(100, 100, SAME_ASPECT_RATIO).apply {
            Assert.assertTrue(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertTrue(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertFalse(shouldClip(50, 50))
            Assert.assertFalse(shouldClip(150, 150))
        }

        Resize(100, 100, EXACTLY).apply {
            Assert.assertTrue(shouldClip(100, 50))
            Assert.assertTrue(shouldClip(100, 150))
            Assert.assertTrue(shouldClip(50, 100))
            Assert.assertTrue(shouldClip(150, 100))
            Assert.assertFalse(shouldClip(100, 100))
            Assert.assertTrue(shouldClip(50, 50))
            Assert.assertTrue(shouldClip(150, 150))
        }
    }

    /**
     * Calculate the precision according to the original image, and then decide whether to crop the image according to the precision
     */
    private fun Resize.shouldClip(imageWidth: Int, imageHeight: Int): Boolean =
        shouldClip(Size(imageWidth, imageHeight))
}