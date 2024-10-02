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
import com.github.panpf.sketch.state.ConditionStateImage.Condition
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

/**
 * Create an ConditionStateImage
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testConditionStateImage
 */
fun ConditionStateImage(
    defaultDrawable: EquitableDrawable,
    conditionBlock: ConditionStateImage.Builder.() -> Unit
): ConditionStateImage = ConditionStateImage.Builder(DrawableStateImage(defaultDrawable)).apply {
    conditionBlock.invoke(this)
}.build()

/**
 * Create an ConditionStateImage
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testConditionStateImage
 */
fun ConditionStateImage(
    @DrawableRes defaultResId: Int,
    conditionBlock: ConditionStateImage.Builder.() -> Unit
): ConditionStateImage = ConditionStateImage.Builder(DrawableStateImage(defaultResId)).apply {
    conditionBlock.invoke(this)
}.build()

/**
 * Create an ConditionStateImage
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testConditionStateImage
 */
fun ConditionStateImage(
    defaultColor: IntColor,
    conditionBlock: ConditionStateImage.Builder.() -> Unit
): ConditionStateImage = ConditionStateImage.Builder(ColorDrawableStateImage(defaultColor)).apply {
    conditionBlock.invoke(this)
}.build()

/**
 * Create an ConditionStateImage
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testConditionStateImage
 */
fun ConditionStateImage(
    defaultColor: ResColor,
    conditionBlock: ConditionStateImage.Builder.() -> Unit
): ConditionStateImage = ConditionStateImage.Builder(ColorDrawableStateImage(defaultColor)).apply {
    conditionBlock.invoke(this)
}.build()


/**
 * Add a custom state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testAddState
 */
fun ConditionStateImage.Builder.addState(
    condition: Condition,
    drawable: EquitableDrawable
): ConditionStateImage.Builder = apply {
    addState(condition, DrawableStateImage(drawable))
}

/**
 * Add a custom state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testAddState
 */
fun ConditionStateImage.Builder.addState(
    condition: Condition,
    @DrawableRes resId: Int
): ConditionStateImage.Builder = apply {
    addState(condition, DrawableStateImage(resId))
}

/**
 * Add a custom state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testAddState
 */
fun ConditionStateImage.Builder.addState(
    condition: Condition,
    color: IntColor
): ConditionStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}

/**
 * Add a custom state
 *
 * @see com.github.panpf.sketch.core.android.test.state.ConditionStateImageAndroidTest.testAddState
 */
fun ConditionStateImage.Builder.addState(
    condition: Condition,
    color: ResColor
): ConditionStateImage.Builder = apply {
    addState(condition, ColorDrawableStateImage(color))
}