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

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor

/**
 * Create a [ColorDrawableStateImage] that uses the specified color as the drawable
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorDrawableStateImageComposeAndroidTest.testRememberColorDrawableStateImageWithInt
 */
@Composable
fun rememberColorDrawableStateImageWithInt(@ColorInt color: Int): ColorDrawableStateImage =
    remember(color) { IntColorDrawableStateImage(color) }

/**
 * Create a [ColorDrawableStateImage] that uses the specified color resource as the drawable
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorDrawableStateImageComposeAndroidTest.testRememberColorDrawableStateImageWithRes
 */
@Composable
fun rememberColorDrawableStateImageWithRes(@ColorRes resId: Int): ColorDrawableStateImage =
    remember(resId) { ResColorDrawableStateImage(resId) }

/**
 * Create a [ColorDrawableStateImage] that uses the specified color as the drawable
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorDrawableStateImageComposeAndroidTest.testRememberColorDrawableStateImage
 */
@Composable
fun rememberColorDrawableStateImage(color: IntColor): ColorDrawableStateImage =
    remember(color) { ColorDrawableStateImage(color) }

/**
 * Create a [ColorDrawableStateImage] that uses the specified color as the drawable
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorDrawableStateImageComposeAndroidTest.testRememberColorDrawableStateImage
 */
@Composable
fun rememberColorDrawableStateImage(color: ResColor): ColorDrawableStateImage =
    remember(color) { ColorDrawableStateImage(color) }

/**
 * Create a [ColorDrawableStateImage] that uses the specified color as the drawable
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.ColorDrawableStateImageComposeAndroidTest.testRememberColorDrawableStateImage
 */
@Composable
fun rememberColorDrawableStateImage(color: ColorFetcher): ColorDrawableStateImage =
    remember(color) { ColorDrawableStateImage(color) }