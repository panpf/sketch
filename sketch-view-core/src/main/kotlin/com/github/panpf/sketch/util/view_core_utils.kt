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

package com.github.panpf.sketch.util

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Looper
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.resize.Scale
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testAnyAsOrNull
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Returns a string representation of this Int value in the specified radix.
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testAnyToHexString
 */
internal fun Any.toHexString(): String = this.hashCode().toString(16)

/**
 * Check if the current thread is the UI thread
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testRequiredMainThread
 */
internal fun requiredMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This method must be executed in the UI thread"
    }
}

/**
 * Whether the ScaleType is a fit scale type
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testFitScale
 */
internal val ScaleType.fitScale: Boolean
    get() = this == ScaleType.FIT_START
            || this == ScaleType.FIT_CENTER
            || this == ScaleType.FIT_END
            || this == ScaleType.CENTER_INSIDE


/**
 * Find the last child [Drawable] from the specified Drawable
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testFindLeafChildDrawable
 */
fun Drawable.findLeafChildDrawable(): Drawable? {
    return when (val drawable = this) {
        is com.github.panpf.sketch.drawable.CrossfadeDrawable -> {
            drawable.end?.findLeafChildDrawable()
        }

        is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            if (layerCount > 0) {
                drawable.getDrawable(layerCount - 1).findLeafChildDrawable()
            } else {
                null
            }
        }

        else -> drawable
    }
}

/**
 * Calculate the bounds of the image after scaling
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testCalculateBounds
 */
internal fun calculateBounds(srcSize: Size, dstSize: Size, scale: Scale): Rect {
    if (srcSize.isEmpty || dstSize.isEmpty) {
        return Rect(
            /* left = */ 0,
            /* top = */ 0,
            /* right = */ srcSize.width.takeIf { it > 0 } ?: dstSize.width,
            /* bottom = */ srcSize.height.takeIf { it > 0 } ?: dstSize.height
        )
    }

    val srcWidthScaleFactor = dstSize.width.toFloat() / srcSize.width
    val srcHeightScaleFactor = dstSize.height.toFloat() / srcSize.height
    val srcScaleFactor = max(srcWidthScaleFactor, srcHeightScaleFactor)
    val srcScaledWidth = (srcSize.width * srcScaleFactor).roundToInt()
    val srcScaledHeight = (srcSize.height * srcScaleFactor).roundToInt()
    return when (scale) {
        Scale.START_CROP -> {
            Rect(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ srcScaledWidth,
                /* bottom = */ srcScaledHeight
            )
        }

        Scale.CENTER_CROP -> {
            val left: Int = -(srcScaledWidth - dstSize.width) / 2
            val top: Int = -(srcScaledHeight - dstSize.height) / 2
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom = */ top + srcScaledHeight,
            )
        }

        Scale.END_CROP -> {
            val left = -(srcScaledWidth - dstSize.width)
            val top = -(srcScaledHeight - dstSize.height)
            Rect(
                /* left = */ left,
                /* top = */ top,
                /* right = */ left + srcScaledWidth,
                /* bottom =*/ top + srcScaledHeight,
            )
        }

        Scale.FILL -> {
            Rect(
                /* left = */ 0,
                /* top = */ 0,
                /* right = */ dstSize.width,
                /* bottom = */ dstSize.height,
            )
        }
    }
}