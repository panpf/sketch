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

package com.github.panpf.sketch.state

import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.state.ErrorStateImage.Condition
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor


/**
 * Add a custom error state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ErrorStateImageAndroidTest.testAddState
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    drawable: DrawableEqualizer
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(drawable))
}

/**
 * Add a custom error state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ErrorStateImageAndroidTest.testAddState
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    resId: Int
): ErrorStateImage.Builder = apply {
    addState(condition, DrawableStateImage(resId))
}

/**
 * Add a custom error state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ErrorStateImageAndroidTest.testAddState
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    color: IntColor
): ErrorStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}

/**
 * Add a custom error state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ErrorStateImageAndroidTest.testAddState
 */
fun ErrorStateImage.Builder.addState(
    condition: Condition,
    color: ResColor
): ErrorStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}