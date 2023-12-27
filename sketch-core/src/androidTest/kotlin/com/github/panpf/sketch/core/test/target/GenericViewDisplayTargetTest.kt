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
package com.github.panpf.sketch.core.test.target

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.target.GenericViewDisplayTarget
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenericViewDisplayTargetTest {

    @Test
    fun testIsStarted() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewDisplayTarget(imageView).apply {
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(getFieldValue<Boolean>("isStarted")!!)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(getFieldValue<Boolean>("isStarted")!!)
        }
    }

    @Test
    fun testAnimatableDrawable() {
        val context = getTestContext()
        val imageView = ImageView(context)

        TestViewDisplayTarget(imageView).apply {
            val drawable = ColorDrawable(Color.RED)
            onStart(drawable)
            onError(drawable)
            onSuccess(drawable)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)

            onStart(drawable)
            onError(drawable)
            onSuccess(drawable)
        }

        TestViewDisplayTarget(imageView).apply {
            val animatableDrawable = AnimatableDrawable(Color.RED)
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(animatableDrawable)
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onStop(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertFalse(animatableDrawable.running)

            onStart(GlobalLifecycle.getFieldValue<LifecycleOwner>("owner")!!)
            Assert.assertTrue(animatableDrawable.running)

            onSuccess(ColorDrawable(Color.RED))
            Assert.assertFalse(animatableDrawable.running)

            onSuccess(animatableDrawable)
            Assert.assertTrue(animatableDrawable.running)
        }
    }

    class TestViewDisplayTarget(override val view: ImageView) :
        GenericViewDisplayTarget<ImageView>() {
        override val supportDisplayCount: Boolean = false
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
    }

    class AnimatableDrawable(color: Int) : ColorDrawable(color), Animatable {
        var running = false

        override fun start() {
            running = true
        }

        override fun stop() {
            running = false
        }

        override fun isRunning(): Boolean {
            return running
        }
    }
}