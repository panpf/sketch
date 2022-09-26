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
package com.github.panpf.sketch.test.stateimage.internal

import android.R.drawable
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.stateimage.internal.SketchStateAnimatableDrawable
import com.github.panpf.sketch.stateimage.internal.SketchStateNormalDrawable
import com.github.panpf.sketch.stateimage.internal.toSketchStateDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.getDrawableCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchStateNormalDrawableTest {

    @Test
    fun testTryToSketchStateDrawable() {
        val context = getTestContext()
        val resources = context.resources

        val imageUri = newAssetUri("sample.jpeg")
        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))

        bitmapDrawable.toSketchStateDrawable().let { it as SketchStateNormalDrawable }.apply {
            Assert.assertNotSame(bitmapDrawable, this)
            Assert.assertSame(bitmapDrawable, wrappedDrawable)
        }

        val animDrawable = SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(bitmapDrawable),
            imageUri = imageUri,
            requestKey = imageUri,
            requestCacheKey = imageUri,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null,
            extras = null,
        )
        animDrawable.toSketchStateDrawable().let { it as SketchStateAnimatableDrawable }.apply {
            Assert.assertNotSame(animDrawable, this)
            Assert.assertSame(animDrawable, wrappedDrawable)
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        SketchStateNormalDrawable(
            drawable = context.getDrawableCompat(drawable.bottom_bar),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }

        SketchStateNormalDrawable(
            drawable = TestNewMutateDrawable(context.getDrawableCompat(drawable.bottom_bar)),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                Assert.assertEquals(Size(100, 200), intrinsicSize)
            }

        SketchStateNormalDrawable(bitmapDrawable).apply {
            Assert.assertEquals("SketchStateNormalDrawable($bitmapDrawable)", toString())
        }
    }
}