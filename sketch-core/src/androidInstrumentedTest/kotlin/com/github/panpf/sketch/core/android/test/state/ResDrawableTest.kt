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
package com.github.panpf.sketch.core.android.test.state

import android.R
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResDrawableTest {

    @Test
    fun testGetDrawable() {
        val context = getTestContext()

        ResDrawable(R.drawable.ic_delete).apply {
            Assert.assertSame(
                context.getDrawableCompat(android.R.drawable.ic_delete)
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }

        ResDrawable(R.drawable.bottom_bar).apply {
            Assert.assertSame(
                context.getDrawableCompat(android.R.drawable.bottom_bar)
                    .asOrThrow<BitmapDrawable>().bitmap,
                getDrawable(context).asOrThrow<BitmapDrawable>().bitmap
            )
        }
    }

    @Test
    fun testToString() {
        ResDrawable(R.drawable.ic_delete).apply {
            Assert.assertEquals("ResDrawable(${android.R.drawable.ic_delete})", toString())
        }

        ResDrawable(R.drawable.bottom_bar).apply {
            Assert.assertEquals("ResDrawable(${android.R.drawable.bottom_bar})", toString())
        }

        val context = getTestContext()
        ResDrawable(context.packageName, context.resources, R.drawable.bottom_bar).apply {
            Assert.assertEquals(
                "ResDrawable(packageName=${context.packageName}, resources=${context.resources}, resId=${android.R.drawable.bottom_bar})",
                toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResDrawable(R.drawable.ic_delete)
        val element11 = ResDrawable(R.drawable.ic_delete)
        val element2 = ResDrawable(R.drawable.bottom_bar)
        val element3 = ResDrawable(R.drawable.btn_dialog)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }
}