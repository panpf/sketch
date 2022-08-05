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
package com.github.panpf.sketch.test.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.sizeString
import com.github.panpf.sketch.util.pool.AttributeStrategy
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttributeStrategyTest {

    @Test
    fun testPutGet() {
        AttributeStrategy().apply {
            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertNotNull(get(100, 100, ARGB_8888))

            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertNotNull(get(100, 100, ARGB_8888))
            Assert.assertNotNull(get(100, 100, ARGB_8888))
        }

        AttributeStrategy().apply {
            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(100, 100, ARGB_8888))
            Assert.assertNotNull(get(100, 100, ARGB_8888))
            Assert.assertNotNull(get(100, 100, ARGB_8888))
        }

        AttributeStrategy().apply {
            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(1000, 10, ARGB_8888))
            Assert.assertNull(get(100, 100, ARGB_8888))
            Assert.assertNotNull(get(1000, 10, ARGB_8888))
        }

        AttributeStrategy().apply {
            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(110, 110, ARGB_8888))
            Assert.assertNull(get(100, 100, ARGB_8888))
            Assert.assertNotNull(get(110, 110, ARGB_8888))
        }

        AttributeStrategy().apply {
            Assert.assertNull(get(100, 100, ARGB_8888))
            put(Bitmap.createBitmap(90, 90, ARGB_8888))
            Assert.assertNull(get(100, 100, ARGB_8888))
            Assert.assertNotNull(get(90, 90, ARGB_8888))
        }
    }

    @Test
    fun testRemoveLast() {
        AttributeStrategy().apply {
            Assert.assertNull(removeLast())
            put(Bitmap.createBitmap(50, 50, ARGB_8888))
            put(Bitmap.createBitmap(70, 70, ARGB_8888))
            put(Bitmap.createBitmap(90, 90, ARGB_8888))
            Assert.assertEquals("90x90", removeLast()!!.sizeString)
            Assert.assertEquals("70x70", removeLast()!!.sizeString)
            Assert.assertEquals("50x50", removeLast()!!.sizeString)
        }
    }

    @Test
    fun testLogBitmap() {
        AttributeStrategy().apply {
            Assert.assertEquals(
                "[50x50](ARGB_8888)",
                logBitmap(Bitmap.createBitmap(50, 50, ARGB_8888))
            )
            Assert.assertEquals(
                "[70x70](RGB_565)",
                logBitmap(Bitmap.createBitmap(70, 70, RGB_565))
            )

            Assert.assertEquals("[50x50](ARGB_8888)", logBitmap(50, 50, ARGB_8888))
            Assert.assertEquals("[70x70](RGB_565)", logBitmap(70, 70, RGB_565))
        }
    }

    @Test
    fun testGetSize() {
        AttributeStrategy().apply {
            Assert.assertEquals(10000, getSize(Bitmap.createBitmap(50, 50, ARGB_8888)))
            Assert.assertEquals(5000, getSize(Bitmap.createBitmap(50, 50, RGB_565)))
        }
    }

    @Test
    fun testToString() {
        AttributeStrategy().apply {
            Assert.assertNull(removeLast())
            put(Bitmap.createBitmap(50, 50, ARGB_8888))
            put(Bitmap.createBitmap(70, 70, RGB_565))
            Assert.assertEquals(
                "AttributeStrategy(GroupedLinkedMap({[50x50](ARGB_8888):1}, {[70x70](RGB_565):1}))",
                toString()
            )
        }
    }
}