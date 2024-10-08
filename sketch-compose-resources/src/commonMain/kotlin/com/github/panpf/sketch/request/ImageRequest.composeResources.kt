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

package com.github.panpf.sketch.request

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.state.ComposableErrorStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import org.jetbrains.compose.resources.DrawableResource

/**
 * Set Drawable placeholder image when loading
 */
@Composable
fun ImageRequest.Builder.placeholder(resource: DrawableResource): ImageRequest.Builder =
    placeholder(rememberPainterStateImage(resource))

/**
 * Set Drawable placeholder image when uri is invalid
 */
@Composable
fun ImageRequest.Builder.fallback(resource: DrawableResource): ImageRequest.Builder =
    fallback(rememberPainterStateImage(resource))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 */
@Composable
fun ImageRequest.Builder.error(
    defaultResource: DrawableResource,
): ImageRequest.Builder = error(ComposableErrorStateImage(defaultResource))

/**
 * Set Color image to display when loading fails.
 *
 * You can also set image of different error types via the trailing lambda function
 *
 * [configBlock] must be inline so that the status used internally will be correctly monitored and updated.
 */
@Composable
inline fun ImageRequest.Builder.composableError(
    defaultResource: DrawableResource,
    crossinline configBlock: @Composable (ErrorStateImage.Builder.() -> Unit)
): ImageRequest.Builder = error(ComposableErrorStateImage(defaultResource, configBlock))