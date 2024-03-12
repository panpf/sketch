/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.drawable.internal

import android.graphics.Rect
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.Size
import kotlin.math.min

internal fun calculateFitBounds(contentSize: Size, containerBounds: Rect): Rect {
    val left: Int
    val top: Int
    val right: Int
    val bottom: Int
    if (contentSize.width <= containerBounds.width() && contentSize.height <= containerBounds.height()) {
        left = containerBounds.left + (containerBounds.width() - contentSize.width) / 2
        top = containerBounds.top + (containerBounds.height() - contentSize.height) / 2
        right = left + contentSize.width
        bottom = top + contentSize.height
    } else {
        val scale = min(
            containerBounds.width().toFloat() / contentSize.width,
            containerBounds.height().toFloat() / contentSize.height
        )
        val scaledWidth = (contentSize.width * scale).toInt()
        val scaledHeight = (contentSize.height * scale).toInt()
        left = containerBounds.left + (containerBounds.width() - scaledWidth) / 2
        top = containerBounds.top + (containerBounds.height() - scaledHeight) / 2
        right = left + scaledWidth
        bottom = top + scaledHeight
    }
    return Rect(left, top, right, bottom)
}

fun Drawable.toLogString(): String = when {
    this is SketchDrawable -> toString()
    this is BitmapDrawable -> "BitmapDrawable(${bitmap.toLogString()})"
    this is RoundedBitmapDrawable -> "RoundedBitmapDrawable(drawable=${bitmap?.toLogString()})"
    VERSION.SDK_INT >= VERSION_CODES.P && this is AnimatedImageDrawable -> "AnimatedImageDrawable(${toSizeString()})"
    this is AnimatedVectorDrawableCompat -> "AnimatedVectorDrawableCompat(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && this is AnimatedVectorDrawable -> "AnimatedVectorDrawable(${toSizeString()})"
    this is TransitionDrawable -> "TransitionDrawable(${toSizeString()})"
    this is ColorDrawable -> "ColorDrawable(${color})"
    VERSION.SDK_INT >= VERSION_CODES.Q && this is ColorStateListDrawable -> "ColorStateListDrawable(${toSizeString()})"
    this is VectorDrawableCompat -> "VectorDrawableCompat(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && this is VectorDrawable -> "VectorDrawable(${toSizeString()})"
    this is GradientDrawable -> "GradientDrawable(${toSizeString()})"
    VERSION.SDK_INT >= VERSION_CODES.M && this is DrawableWrapper -> "DrawableWrapper(drawable=${drawable?.toLogString()})"
    this is DrawableWrapperCompat -> "DrawableWrapperCompat(drawable=${drawable?.toLogString()})"
    else -> toString()
}

internal fun Drawable.toSizeString(): String = "${intrinsicWidth}x${intrinsicHeight}"