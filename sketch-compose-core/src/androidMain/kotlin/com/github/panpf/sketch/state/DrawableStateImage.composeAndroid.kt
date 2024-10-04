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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.drawable.EquitableDrawable

/**
 * Create a [DrawableStateImage] instance and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.DrawableStateImageComposeAndroidTest.testRememberDrawableStateImage
 */
@Composable
fun rememberDrawableStateImage(drawable: EquitableDrawable): DrawableStateImage =
    remember(drawable) { DrawableStateImage(drawable) }

/**
 * Create a [DrawableStateImage] instance and remember it.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.state.DrawableStateImageComposeAndroidTest.testRememberDrawableStateImage
 */
@Composable
fun rememberDrawableStateImage(@DrawableRes resId: Int): DrawableStateImage =
    remember(resId) { DrawableStateImage(resId) }
