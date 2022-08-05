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
package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.util.asOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawableStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))

        DrawableStateImage(ColorDrawable(Color.BLUE)).apply {
            Assert.assertEquals(
                Color.BLUE,
                getDrawable(sketch, request, null).asOrNull<ColorDrawable>()!!.color
            )
        }

        DrawableStateImage(ColorDrawable(Color.GREEN)).apply {
            Assert.assertEquals(
                Color.GREEN,
                getDrawable(sketch, request, null).asOrNull<ColorDrawable>()!!.color
            )
        }

        DrawableStateImage(android.R.drawable.btn_radio).apply {
            Assert.assertTrue(getDrawable(sketch, request, null) is StateListDrawable)
        }
    }

    @Test
    fun testEquals() {
        val stateImage1 = DrawableStateImage(android.R.drawable.btn_radio)
        val stateImage11 = DrawableStateImage(android.R.drawable.btn_radio)

        val stateImage2 = DrawableStateImage(android.R.drawable.btn_dialog)
        val stateImage21 = DrawableStateImage(android.R.drawable.btn_dialog)

        val stateImage3 = DrawableStateImage(android.R.drawable.btn_plus)
        val stateImage31 = DrawableStateImage(android.R.drawable.btn_plus)

        Assert.assertNotSame(stateImage1, stateImage11)
        Assert.assertNotSame(stateImage2, stateImage21)
        Assert.assertNotSame(stateImage3, stateImage31)

        Assert.assertEquals(stateImage1, stateImage11)
        Assert.assertEquals(stateImage2, stateImage21)
        Assert.assertEquals(stateImage3, stateImage31)

        Assert.assertNotEquals(stateImage1, stateImage2)
        Assert.assertNotEquals(stateImage1, stateImage3)
        Assert.assertNotEquals(stateImage2, stateImage3)
    }

    @Test
    fun testHashCode() {
        val stateImage1 = DrawableStateImage(android.R.drawable.btn_radio)
        val stateImage11 = DrawableStateImage(android.R.drawable.btn_radio)

        val stateImage2 = DrawableStateImage(android.R.drawable.btn_dialog)
        val stateImage21 = DrawableStateImage(android.R.drawable.btn_dialog)

        val stateImage3 = DrawableStateImage(android.R.drawable.btn_plus)
        val stateImage31 = DrawableStateImage(android.R.drawable.btn_plus)

        Assert.assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        Assert.assertEquals(stateImage2.hashCode(), stateImage21.hashCode())
        Assert.assertEquals(stateImage3.hashCode(), stateImage31.hashCode())

        Assert.assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
        Assert.assertNotEquals(stateImage1.hashCode(), stateImage3.hashCode())
        Assert.assertNotEquals(stateImage2.hashCode(), stateImage3.hashCode())
    }

    @Test
    fun testToString() {
        DrawableStateImage(android.R.drawable.btn_radio).apply {
            Assert.assertEquals(
                "DrawableStateImage(ResDrawable(${android.R.drawable.btn_radio}))",
                toString()
            )
        }
        DrawableStateImage(android.R.drawable.btn_dialog).apply {
            Assert.assertEquals(
                "DrawableStateImage(ResDrawable(${android.R.drawable.btn_dialog}))",
                toString()
            )
        }
    }
}