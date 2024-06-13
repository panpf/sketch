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
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestTarget
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrentStateImageAndroidTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val request = ImageRequest(imageView, MyImages.jpeg.uri)

        CurrentStateImage().apply {
            Assert.assertNull(
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
        }

        CurrentStateImage(R.drawable.btn_default).apply {
            Assert.assertTrue(
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable is StateListDrawable
            )
        }

        CurrentStateImage(ColorDrawableEqualizer(Color.RED)).apply {
            Assert.assertTrue(
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable is ColorDrawable
            )
        }

        imageView.setImageDrawable(
            BitmapDrawable(
                context.resources,
                Bitmap.createBitmap(100, 100, ARGB_8888)
            )
        )
        CurrentStateImage(ColorDrawableEqualizer(Color.RED)).apply {
            Assert.assertTrue(
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable is BitmapDrawable
            )
        }

        val request1 = ImageRequest(context, MyImages.jpeg.uri) {
            target(TestTarget())
        }
        CurrentStateImage(ColorDrawableEqualizer(Color.RED)).apply {
            Assert.assertTrue(
                getImage(sketch, request1, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable is ColorDrawable
            )
        }
    }

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val imageView = ImageView(context)
        val request = ImageRequest(imageView, MyImages.jpeg.uri)
        val drawable1 = ColorDrawableEqualizer(Color.BLUE)
        val drawable2 = ColorDrawableEqualizer(Color.GREEN)

        Assert.assertNull(imageView.drawable)
        CurrentStateImage().apply {
            Assert.assertNull(getImage(sketch, request, null))
            imageView.setImageDrawable(drawable1.wrapped)
            Assert.assertSame(
                drawable1.wrapped,
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
        }

        imageView.setImageDrawable(null)
        Assert.assertNull(imageView.drawable)
        CurrentStateImage(DrawableStateImage(drawable2)).apply {
            Assert.assertSame(
                drawable2.wrapped,
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
            imageView.setImageDrawable(drawable1.wrapped)
            Assert.assertSame(
                drawable1.wrapped,
                getImage(sketch, request, null)
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = CurrentStateImage(R.drawable.btn_default)
        val element11 = CurrentStateImage(R.drawable.btn_default)
        val element2 = CurrentStateImage()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        CurrentStateImage().apply {
            Assert.assertEquals("CurrentStateImage(null)", toString())
        }
        CurrentStateImage(R.drawable.btn_default).apply {
            Assert.assertEquals(
                "CurrentStateImage(DrawableStateImage(ResDrawable(${android.R.drawable.btn_default})))",
                toString()
            )
        }
    }
}