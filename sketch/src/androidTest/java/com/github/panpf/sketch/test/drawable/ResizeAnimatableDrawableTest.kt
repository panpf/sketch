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
package com.github.panpf.sketch.test.drawable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ResizeAnimatableDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeAnimatableDrawableTest {

    @Test
    fun test() {
        val imageUri = newAssetUri("sample.jpeg")
        val animDrawable = SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(ColorDrawable(Color.GREEN)),
            imageUri = imageUri,
            requestKey = imageUri,
            requestCacheKey = imageUri,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null,
            extras = null,
        )
        ResizeAnimatableDrawable(animDrawable, Resize(100, 500)).apply {
            start()
            stop()
            isRunning

            val callback = object : AnimationCallback() {}
            Assert.assertFalse(unregisterAnimationCallback(callback))
            runBlocking(Dispatchers.Main) {
                registerAnimationCallback(callback)
            }
            Assert.assertTrue(unregisterAnimationCallback(callback))
            clearAnimationCallbacks()
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()
        val imageUri = newAssetUri("sample.jpeg")

        ResizeAnimatableDrawable(
            drawable = SketchAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable1(context.getDrawableCompat(android.R.drawable.bottom_bar)),
                imageUri = imageUri,
                requestKey = imageUri,
                requestCacheKey = imageUri,
                imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
                dataFrom = LOCAL,
                transformedList = null,
                extras = null,
            ),
            resize = Resize(500, 300)
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }

        ResizeAnimatableDrawable(
            drawable = SketchAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable1(
                    TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.bottom_bar))
                ),
                imageUri = imageUri,
                requestKey = imageUri,
                requestCacheKey = imageUri,
                imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
                dataFrom = LOCAL,
                transformedList = null,
                extras = null,
            ),
            resize = Resize(500, 300)
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
        val imageUri = newAssetUri("sample.jpeg")
        val animDrawable = SketchAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(ColorDrawable(Color.GREEN)),
            imageUri = imageUri,
            requestKey = imageUri,
            requestCacheKey = imageUri,
            imageInfo = ImageInfo(100, 200, "image/jpeg", 0),
            dataFrom = LOCAL,
            transformedList = null,
            extras = null,
        )
        ResizeAnimatableDrawable(animDrawable, Resize(100, 500)).apply {
            Assert.assertEquals("ResizeAnimatableDrawable($animDrawable)", toString())
        }
    }
}