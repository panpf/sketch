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

import androidx.annotation.DrawableRes
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor


/**
 * Set the error image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(
    drawable: EquitableDrawable
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, DrawableStateImage(drawable))
}

/**
 * Set the error image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(
    @DrawableRes resId: Int
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, DrawableStateImage(resId))
}

/**
 * Set the error image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(
    color: IntColor
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, ColorDrawableStateImage(color))
}

/**
 * Set the error image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ErrorStateImage.Builder.saveCellularTrafficError(
    color: ResColor
): ErrorStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, ColorDrawableStateImage(color))
}