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

package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class ResDrawableTest {

    @Test
    fun testKey() {
        val context = getTestContext()
        assertEquals(
            expected = "ResDrawable(${android.R.drawable.ic_delete})",
            actual = ResDrawable(android.R.drawable.ic_delete).key
        )
        assertEquals(
            expected = "ResDrawable(resId=${android.R.drawable.ic_delete},resources=${context.resources},packageName=${context.packageName})",
            actual = ResDrawable(
                android.R.drawable.ic_delete,
                context.resources,
                context.packageName
            ).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()

        ResDrawable(android.R.drawable.ic_delete).apply {
            assertSame(
                context.getDrawableCompat(android.R.drawable.ic_delete)
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }

        ResDrawable(android.R.drawable.ic_lock_lock).apply {
            assertSame(
                context.getDrawableCompat(android.R.drawable.ic_lock_lock)
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = ResDrawable(android.R.drawable.ic_delete)
        val element11 = ResDrawable(android.R.drawable.ic_delete)
        val element2 = ResDrawable(android.R.drawable.ic_lock_lock)
        val element3 = ResDrawable(android.R.drawable.ic_delete, resources = context.resources)
        val element4 = ResDrawable(android.R.drawable.ic_delete, packageName = context.packageName)

        assertEquals(element1, element1)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        assertEquals(
            expected = "ResDrawable(${android.R.drawable.ic_delete})",
            actual = ResDrawable(android.R.drawable.ic_delete).toString()
        )
        assertEquals(
            expected = "ResDrawable(resId=${android.R.drawable.ic_delete}, resources=${context.resources}, packageName=${context.packageName})",
            actual = ResDrawable(
                android.R.drawable.ic_delete,
                context.resources,
                context.packageName
            ).toString()
        )
    }
}