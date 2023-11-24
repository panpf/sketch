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
package com.github.panpf.sketch.core.test.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.pool.GroupedLinkedMap
import com.github.panpf.sketch.util.pool.Poolable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupedLinkedMapTest {

    @Test
    fun testPutGet() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)

            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))

            put(bitmap.toKey(), bitmap)
            Assert.assertNotNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))

            put(bitmap2.toKey(), bitmap2)
            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNotNull(get(bitmap2.toKey()))

            Assert.assertNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))
        }
    }

    @Test
    fun testRemoveLast() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)
            put(bitmap.toKey(), bitmap)
            put(bitmap2.toKey(), bitmap2)
            removeLast()

            Assert.assertNotNull(get(bitmap.toKey()))
            Assert.assertNull(get(bitmap2.toKey()))
        }
    }

    @Test
    fun testToString() {
        GroupedLinkedMap<MyKey, Bitmap>().apply {
            val bitmap = Bitmap.createBitmap(100, 100, ARGB_8888)
            put(bitmap.toKey(), bitmap)
            val bitmap2 = Bitmap.createBitmap(200, 100, ARGB_8888)
            put(bitmap2.toKey(), bitmap2)
            Assert.assertEquals(
                "GroupedLinkedMap({MyKey(key=100x100(ARGB_8888)):1}, {MyKey(key=200x100(ARGB_8888)):1})",
                toString()
            )
        }
    }

    private fun Bitmap.toKey(): MyKey = MyKey("${width}x${height}($config)")

    data class MyKey(val key: String) : Poolable {
        override fun offer() {

        }
    }
}