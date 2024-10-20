/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.util

import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.vectordrawable.graphics.drawable.Animatable2Compat

/**
 * Check if the current thread is the UI thread
 *
 * @see com.github.panpf.sketch.animated.core.android.test.util.AnimatedUtilsTest.testRequiredMainThread
 */
internal fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

/**
 * Wrap onStart and onEnd into [Animatable2Compat.AnimationCallback]
 *
 * @see com.github.panpf.sketch.animated.core.android.test.util.AnimatedUtilsTest.testAnimatable2CompatCallbackOf
 */
fun animatable2CompatCallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
): Animatable2Compat.AnimationCallback = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}