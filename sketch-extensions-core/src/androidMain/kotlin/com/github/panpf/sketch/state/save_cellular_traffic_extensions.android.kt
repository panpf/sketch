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
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher


/**
 * Set the state image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ConditionStateImage.Builder.saveCellularTrafficError(
    drawable: EquitableDrawable
): ConditionStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, DrawableStateImage(drawable))
}

/**
 * Set the state image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ConditionStateImage.Builder.saveCellularTrafficError(
    @DrawableRes resId: Int
): ConditionStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, DrawableStateImage(resId))
}

/**
 * Set the state image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ConditionStateImage.Builder.saveCellularTrafficError(
    color: IntColorFetcher
): ConditionStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, ColorDrawableStateImage(color))
}

/**
 * Set the state image when the save cellular traffic
 *
 * @see com.github.panpf.sketch.extensions.core.android.test.state.SaveCellularTrafficExtensionsAndroidTest.testSaveCellularTrafficError
 */
fun ConditionStateImage.Builder.saveCellularTrafficError(
    color: ResColorFetcher
): ConditionStateImage.Builder = apply {
    addState(SaveCellularTrafficCondition, ColorDrawableStateImage(color))
}