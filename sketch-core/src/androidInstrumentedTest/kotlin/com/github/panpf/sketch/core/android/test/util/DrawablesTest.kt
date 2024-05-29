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
package com.github.panpf.sketch.core.android.test.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.findLeafChildDrawable
import com.github.panpf.sketch.util.toNewBitmap
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawablesTest {

    @Test
    fun testToNewBitmap() {
        val context = getTestContext()

        val drawable = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )

        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)
        drawable.toNewBitmap().apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertEquals("AndroidBitmap(100x100,ARGB_8888)", toShortInfoString())
        }
        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)

        drawable.setBounds(100, 100, 200, 200)
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
        drawable.toNewBitmap(Bitmap.Config.RGB_565).apply {
            Assert.assertEquals(Bitmap.Config.RGB_565, config)
            Assert.assertEquals("AndroidBitmap(100x100,RGB_565)", toShortInfoString())
        }
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
    }
}