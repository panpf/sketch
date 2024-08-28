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
import org.jetbrains.compose.resources.DrawableResource

/**
 * Create an ErrorStateImage
 */
@Composable
fun ComposableErrorStateImage(
    defaultResource: DrawableResource? = null,
): ErrorStateImage =
    ErrorStateImage.Builder(defaultResource?.let { rememberPainterStateImage(it) }).build()

/**
 * Create an ErrorStateImage
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ComposableErrorStateImage(
    defaultResource: DrawableResource? = null,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ErrorStateImage =
    ErrorStateImage.Builder(defaultResource?.let { rememberPainterStateImage(it) }).apply {
        configBlock.invoke(this)
    }.build()

/**
 * Add a custom error state
 */
@Composable
fun ErrorStateImage.Builder.addState(
    condition: ErrorStateImage.Condition,
    resource: DrawableResource
): ErrorStateImage.Builder = apply {
    addState(condition, rememberPainterStateImage(resource))
}