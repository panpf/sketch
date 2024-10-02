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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Create an ConditionStateImage
 *
 * [conditionBlock] must be inline so that the status used internally will be correctly monitored and updated.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ConditionStateImageComposeTest.testComposableConditionStateImage
 */
@Composable
inline fun ComposableConditionStateImage(
    defaultImage: StateImage,
    crossinline conditionBlock: @Composable (ConditionStateImage.Builder.() -> Unit)
): ConditionStateImage = ConditionStateImage.Builder(defaultImage).apply {
    conditionBlock.invoke(this)
}.build()

/**
 * Create an ConditionStateImage
 *
 * [conditionBlock] must be inline so that the status used internally will be correctly monitored and updated.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ConditionStateImageComposeTest.testComposableConditionStateImage
 */
@Composable
inline fun ComposableConditionStateImage(
    defaultImage: Color,
    crossinline conditionBlock: @Composable (ConditionStateImage.Builder.() -> Unit)
): ConditionStateImage =
    ConditionStateImage.Builder(ColorPainterStateImage(defaultImage)).apply {
        conditionBlock.invoke(this)
    }.build()


/**
 * Add a custom state
 *
 * @see com.github.panpf.sketch.compose.core.common.test.state.ConditionStateImageComposeTest.testAddState
 */
fun ConditionStateImage.Builder.addState(
    condition: ConditionStateImage.Condition,
    color: Color
): ConditionStateImage.Builder = apply {
    addState(condition, ColorPainterStateImage(color))
}