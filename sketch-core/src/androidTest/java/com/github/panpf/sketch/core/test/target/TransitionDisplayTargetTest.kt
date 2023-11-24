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
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransitionDisplayTargetTest {

    @Test
    fun test() {
        val context = getTestContext()
        TestTransitionViewDisplayTarget(ImageView(context)).apply {
            onStart(null)
            onError(null)
            onSuccess(ColorDrawable(Color.RED))
        }
    }

    class TestTransitionViewDisplayTarget(private val view: ImageView) : TransitionDisplayTarget {
        override val supportDisplayCount: Boolean = false
        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }
    }
}