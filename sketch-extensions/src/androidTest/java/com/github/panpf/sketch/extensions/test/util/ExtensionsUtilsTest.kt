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
package com.github.panpf.sketch.extensions.test.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.extensions.test.toShortInfoString
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.getLifecycle
import com.github.panpf.sketch.util.toNewBitmap
import com.github.panpf.tools4a.test.ktx.getActivitySync
import com.github.panpf.tools4a.test.ktx.launchActivity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExtensionsUtilsTest {

    @Test
    fun testFormat() {
        Assert.assertEquals(1.412f, 1.412412f.format(3))
        Assert.assertEquals(1.41f, 1.412412f.format(2))
        Assert.assertEquals(1.4f, 1.412412f.format(1))
        Assert.assertEquals(1f, 1.412412f.format(0))
    }

    @Test
    fun testToNewBitmap() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val bitmapPool = LruBitmapPool(1024 * 1024 * 100)

        val drawable = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )

        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)
        drawable.toNewBitmap(bitmapPool).apply {
            Assert.assertEquals(Bitmap.Config.ARGB_8888, config)
            Assert.assertEquals("Bitmap(100x100,ARGB_8888)", toShortInfoString())
        }
        Assert.assertEquals(Rect(0, 0, 0, 0), drawable.bounds)

        drawable.setBounds(100, 100, 200, 200)
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
        drawable.toNewBitmap(bitmapPool, Bitmap.Config.RGB_565).apply {
            Assert.assertEquals(Bitmap.Config.RGB_565, config)
            Assert.assertEquals("Bitmap(100x100,RGB_565)", toShortInfoString())
        }
        Assert.assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
    }

    @Test
    fun testGetLifecycle() {
        val context = InstrumentationRegistry.getInstrumentation().context
        Assert.assertNull(context.getLifecycle())

        val activity = TestActivity::class.launchActivity().getActivitySync()
        Assert.assertNotNull((activity as Context).getLifecycle())
    }

    class TestActivity : FragmentActivity() {
    }
}