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
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class ResDrawableTest {

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

        ResDrawable(android.R.drawable.bottom_bar).apply {
            assertSame(
                context.getDrawableCompat(android.R.drawable.bottom_bar)
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }
    }

    @Test
    fun testToString() {
        ResDrawable(android.R.drawable.ic_delete).apply {
            assertEquals("ResDrawable(${android.R.drawable.ic_delete})", toString())
        }

        ResDrawable(android.R.drawable.bottom_bar).apply {
            assertEquals("ResDrawable(${android.R.drawable.bottom_bar})", toString())
        }

        val context = getTestContext()
        ResDrawable(
            resId = android.R.drawable.bottom_bar,
            packageName = context.packageName,
            resources = context.resources
        ).apply {
            assertEquals(
                "ResDrawable(packageName=${context.packageName}, resources=${context.resources}, resId=${android.R.drawable.bottom_bar})",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResDrawable(android.R.drawable.ic_delete)
        val element11 = ResDrawable(android.R.drawable.ic_delete)
        val element2 = ResDrawable(android.R.drawable.bottom_bar)
        val element3 = ResDrawable(android.R.drawable.btn_dialog)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }
}