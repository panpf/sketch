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

import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Looper
import android.widget.ImageView.ScaleType
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.resize.Scale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
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
val ScaleType.fitScale: Boolean
    get() = this == ScaleType.FIT_START
            || this == ScaleType.FIT_CENTER
            || this == ScaleType.FIT_END
            || this == ScaleType.CENTER_INSIDE


/**
 * Find the last [Drawable] of the [Drawable]
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testFindLeafDrawable
 */
fun Drawable.findLeafDrawable(): Drawable = when (val drawable = this) {
    is CrossfadeDrawable -> {
        drawable.end?.findLeafDrawable() ?: drawable
    }

    is LayerDrawable -> {
        val layerCount = drawable.numberOfLayers
        if (layerCount > 0) {
            drawable.getDrawable(layerCount - 1).findLeafDrawable()
        } else {
            drawable
        }
    }

    else -> drawable
}

/**
 * Find the deepest [Drawable] of the [Drawable]
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testFindDeepestDrawable
 */
fun Drawable.findDeepestDrawable(): Drawable {
    val drawable = this
    return when {
        drawable is CrossfadeDrawable -> {
            drawable.end?.findDeepestDrawable() ?: drawable
        }

        drawable is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            if (layerCount > 0) {
                drawable.getDrawable(layerCount - 1).findDeepestDrawable()
            } else {
                drawable
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && drawable is DrawableWrapper -> {
            drawable.drawable?.findDeepestDrawable() ?: drawable
        }

        drawable is DrawableWrapperCompat -> {
            drawable.drawable?.findDeepestDrawable() ?: drawable
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
            left = 0,
            top = 0,
            right = srcSize.width.takeIf { it > 0 } ?: dstSize.width,
            bottom = srcSize.height.takeIf { it > 0 } ?: dstSize.height
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
                left = 0,
                top = 0,
                right = srcScaledWidth,
                bottom = srcScaledHeight
            )
        }

        Scale.CENTER_CROP -> {
            val left: Int = -(srcScaledWidth - dstSize.width) / 2
            val top: Int = -(srcScaledHeight - dstSize.height) / 2
            Rect(
                left = left,
                top = top,
                right = left + srcScaledWidth,
                bottom = top + srcScaledHeight,
            )
        }

        Scale.END_CROP -> {
            val left = -(srcScaledWidth - dstSize.width)
            val top = -(srcScaledHeight - dstSize.height)
            Rect(
                left = left,
                top = top,
                right = left + srcScaledWidth,
                bottom = top + srcScaledHeight,
            )
        }

        Scale.FILL -> {
            Rect(
                left = 0,
                top = 0,
                right = dstSize.width,
                bottom = dstSize.height,
            )
        }
    }
}

/**
 * Calculate the bounds of the image after scaling
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testCalculateBoundsWithScaleType
 */
internal fun calculateBoundsWithScaleType(
    srcSize: Size,
    dstSize: Size,
    scaleType: ScaleType
): Rect {
    if (dstSize.isEmpty) {
        return Rect(left = 0, top = 0, 0, bottom = 0)
    }
    if (srcSize.isEmpty) {
        return Rect(left = 0, top = 0, right = dstSize.width, bottom = dstSize.height)
    }

    val widthScaleFactor: Float = dstSize.width.toFloat() / srcSize.width
    val heightScaleFactor: Float = dstSize.height.toFloat() / srcSize.height
    val (scaleFactor: PointF, alignment: Int) = when {
        scaleType == ScaleType.CENTER_CROP -> {
            val maxScaleFactor = max(widthScaleFactor, heightScaleFactor)
            PointF(/* x = */ maxScaleFactor, /* y = */ maxScaleFactor) to 0
        }

        scaleType == ScaleType.CENTER || (scaleType == ScaleType.CENTER_INSIDE && srcSize.width <= dstSize.width && srcSize.height <= dstSize.height) -> {
            PointF(/* x = */ 1f, /* y = */ 1f) to 0
        }

        scaleType == ScaleType.FIT_XY -> {
            PointF(widthScaleFactor, heightScaleFactor) to -1
        }

        scaleType == ScaleType.FIT_START -> {
            val minScaleFactor: Float = min(widthScaleFactor, heightScaleFactor)
            PointF(/* x = */ minScaleFactor, /* y = */ minScaleFactor) to -1
        }

        scaleType == ScaleType.FIT_END -> {
            val minScaleFactor: Float = min(widthScaleFactor, heightScaleFactor)
            PointF(/* x = */ minScaleFactor, /* y = */ minScaleFactor) to 1
        }

        scaleType == ScaleType.MATRIX -> {
            PointF(/* x = */ 1f, /* y = */ 1f) to -1
        }

        else -> {
            // scaleType == ScaleType.FIT_CENTER || (scaleType == ScaleType.CENTER_INSIDE && (srcSize.width > dstSize.width || srcSize.height > dstSize.height))
            val minScaleFactor: Float = min(widthScaleFactor, heightScaleFactor)
            PointF(/* x = */ minScaleFactor, /* y = */ minScaleFactor) to 0
        }
    }
    return calculateBoundsWithScaleAndAlignment(
        srcSize = srcSize,
        dstSize = dstSize,
        scaleFactor = scaleFactor,
        alignment = alignment
    )
}

/**
 * Calculate the bounds of the image after scaling
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testCalculateBoundsWithScaleAndAlignment
 */
fun calculateBoundsWithScaleAndAlignment(
    srcSize: Size,
    dstSize: Size,
    scaleFactor: PointF,
    alignment: Int, // -1: Start, 0: Center, 1: End
): Rect {
    if (dstSize.isEmpty) {
        return Rect(left = 0, top = 0, 0, bottom = 0)
    }
    if (srcSize.isEmpty) {
        return Rect(left = 0, top = 0, right = dstSize.width, bottom = dstSize.height)
    }

    val scaledWidth: Float = srcSize.width * scaleFactor.x
    val scaledHeight: Float = srcSize.height * scaleFactor.y
    val left: Float = if (alignment < 0) {   // Start
        0f
    } else if (alignment > 0) {   // End
        dstSize.width - scaledWidth
    } else { // Center
        (dstSize.width - scaledWidth) / 2f
    }
    val top: Float = if (alignment < 0) {   // Start
        0f
    } else if (alignment > 0) {   // End
        dstSize.height - scaledHeight
    } else { // Center
        (dstSize.height - scaledHeight) / 2f
    }
    return Rect(
        left = floor(left).toInt(),
        top = floor(top).toInt(),
        right = ceil(left + scaledWidth).toInt(),
        bottom = ceil(top + scaledHeight).toInt(),
    )
}


/**
 * Convert [ScaleType] to [Scale]
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testToScale
 */
fun ScaleType.toScale(): Scale = when (this) {
    ScaleType.FIT_START -> Scale.START_CROP
    ScaleType.FIT_CENTER -> Scale.CENTER_CROP
    ScaleType.FIT_END -> Scale.END_CROP
    ScaleType.CENTER_INSIDE -> Scale.CENTER_CROP
    ScaleType.CENTER -> Scale.CENTER_CROP
    ScaleType.CENTER_CROP -> Scale.CENTER_CROP
    else -> Scale.FILL
}


/**
 * Convert [Scale] to [ScaleType]
 *
 * @see com.github.panpf.sketch.view.core.test.util.ViewCoreUtilsTest.testToScaleType
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
fun Scale.toScaleType(): ScaleType = when (this) {
    Scale.START_CROP -> ScaleType.FIT_START
    Scale.CENTER_CROP -> ScaleType.CENTER_CROP
    Scale.END_CROP -> ScaleType.FIT_END
    Scale.FILL -> ScaleType.FIT_XY
    else -> ScaleType.FIT_CENTER
}