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
package com.github.panpf.sketch.core.test.stateimage.internal

import android.R.drawable
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.stateimage.internal.SketchStateAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable1
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.util.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SketchStateAnimatableDrawableTest {

    @Test
    fun test() {
        val animDrawable = SketchStateAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(ColorDrawable(Color.GREEN)),
        )
        SketchStateAnimatableDrawable(animDrawable).apply {
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

        SketchStateAnimatableDrawable(
            animatableDrawable = SketchStateAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable1(context.getDrawableCompat(drawable.bottom_bar)),
            ),
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }

        SketchStateAnimatableDrawable(
            animatableDrawable = SketchStateAnimatableDrawable(
                animatableDrawable = TestAnimatableDrawable1(
                    TestNewMutateDrawable(context.getDrawableCompat(drawable.bottom_bar))
                ),
            ),
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
        val animDrawable = SketchStateAnimatableDrawable(
            animatableDrawable = TestAnimatableDrawable1(ColorDrawable(Color.GREEN)),
        )
        SketchStateAnimatableDrawable(animDrawable).apply {
            Assert.assertEquals("SketchStateAnimatableDrawable($animDrawable)", toString())
        }
    }
}