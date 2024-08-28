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

package com.github.panpf.sketch.sample.ui.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.content.res.ResourcesCompat

@Composable
actual fun windowSize(): IntSize {
    val context = LocalContext.current
    return context.resources.displayMetrics.let { displayMetrics ->
        IntSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}

internal fun Resources.getDrawableCompat(@DrawableRes id: Int, theme: Theme? = null): Drawable {
    return checkNotNull(ResourcesCompat.getDrawable(this, id, theme)) {
        "Can't find drawable by id=$id"
    }
}

internal fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable {
    val drawable = AppCompatResources.getDrawable(this, resId)
    return checkNotNull(drawable) { "Invalid resource ID: $resId" }
}

internal fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun Context.getWindowBackgroundColor(): Int {
    val array = theme.obtainStyledAttributes(
        intArrayOf(android.R.attr.windowBackground)
    )
    val windowBackground = array.getColor(0, 0xFF00FF)
    array.recycle()
    return windowBackground
}

fun Context.isNightMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}