/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size as SketchSize
import kotlin.math.roundToInt

/**
 * Get window container size
 *
 * @see com.github.panpf.sketch.compose.core.android.test.util.AsyncImageStateAndroidTest.testWindowContainerSize
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.util.AsyncImageStateNonAndroidTest.testWindowContainerSize
 */
@Composable
expect fun windowContainerSize(): IntSize

/**
 * Convert [ContentScale] to [Scale]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testContentScaleToScale
 */
@Stable
@Deprecated("Use `toScale(ContentScale, Alignment)` instead")
fun ContentScale.toScale(): Scale {
    return when (this) {
        ContentScale.FillBounds,
        ContentScale.FillWidth,
        ContentScale.FillHeight -> Scale.FILL

        ContentScale.Fit -> Scale.CENTER_CROP
        ContentScale.Crop -> Scale.CENTER_CROP
        ContentScale.Inside -> Scale.CENTER_CROP
        ContentScale.None -> Scale.CENTER_CROP
        else -> Scale.CENTER_CROP
    }
}

/**
 * Convert [ContentScale] to [Scale]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testContentScaleToScale
 */
@Stable
fun toScale(contentScale: ContentScale, alignment: Alignment): Scale = when (contentScale) {
    ContentScale.FillBounds, ContentScale.FillWidth, ContentScale.FillHeight -> {
        Scale.FILL
    }

    else -> {
        // ContentScale.Fit, ContentScale.Crop, ContentScale.Inside, ContentScale.None
        when (alignment) {
            Alignment.TopStart -> Scale.START_CROP
            Alignment.TopCenter -> Scale.CENTER_CROP
            Alignment.TopEnd -> Scale.END_CROP
            Alignment.CenterStart -> Scale.CENTER_CROP
            Alignment.Center -> Scale.CENTER_CROP
            Alignment.CenterEnd -> Scale.CENTER_CROP
            Alignment.BottomStart -> Scale.START_CROP
            Alignment.BottomCenter -> Scale.CENTER_CROP
            Alignment.BottomEnd -> Scale.END_CROP
            else -> Scale.CENTER_CROP
        }
    }
}

/**
 * Convert [ContentScale] to [Scale]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testFromScale
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
@Stable
fun fromScale(scale: Scale): Pair<ContentScale, Alignment> = when (scale) {
    Scale.START_CROP -> ContentScale.Crop to Alignment.TopStart
    Scale.CENTER_CROP -> ContentScale.Crop to Alignment.Center
    Scale.END_CROP -> ContentScale.Crop to Alignment.TopEnd
    Scale.FILL -> ContentScale.FillBounds to Alignment.Center
    else -> ContentScale.Fit to Alignment.Center
}

/**
 * Get the name of the [ContentScale]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testContentScaleName
 */
@Stable
internal val ContentScale.name: String
    get() = when (this) {
        ContentScale.FillWidth -> "FillWidth"
        ContentScale.FillHeight -> "FillHeight"
        ContentScale.FillBounds -> "FillBounds"
        ContentScale.Fit -> "Fit"
        ContentScale.Crop -> "Crop"
        ContentScale.Inside -> "Inside"
        ContentScale.None -> "None"
        else -> this::class.simpleName ?: toString()
    }

/**
 * Whether the [ContentScale] is a fit scale
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testContentScaleFitScale
 */
val ContentScale.fitScale: Boolean
    get() = this == ContentScale.Fit || this == Companion.Inside

/**
 * Returns the name of [Alignment]
 */
@Stable
internal val Alignment.name: String
    get() = when (this) {
        Alignment.TopStart -> "TopStart"
        Alignment.TopCenter -> "TopCenter"
        Alignment.TopEnd -> "TopEnd"
        Alignment.CenterStart -> "CenterStart"
        Alignment.Center -> "Center"
        Alignment.CenterEnd -> "CenterEnd"
        Alignment.BottomStart -> "BottomStart"
        Alignment.BottomCenter -> "BottomCenter"
        Alignment.BottomEnd -> "BottomEnd"
        else -> "Unknown Alignment: $this"
    }

internal fun Alignment.floatAlign(size: Size, space: Size): Offset {
    val horizontalBias = if (this is BiasAlignment) this.horizontalBias else 0f
    val verticalBias = if (this is BiasAlignment) this.verticalBias else 0f
    // Convert to Px first and only round at the end, to avoid rounding twice while calculating
    // the new positions
    val centerX = (space.width - size.width) / 2f
    val centerY = (space.height - size.height) / 2f
    val x = centerX * (1 + horizontalBias)
    val y = centerY * (1 + verticalBias)
    return Offset(x, y)
}

/**
 * Convert [Size] to [IntSize] or return null if it is [Size.isUnspecified]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testSizeToIntSizeOrNull
 */
internal fun Size.toIntSizeOrNull(): IntSize? = when {
    isUnspecified -> null

    width >= 0.5 && height >= 0.5 && width.isFinite() && height.isFinite() -> IntSize(
        width.roundToInt(),
        height.roundToInt()
    )

    else -> null
}

/**
 * Get log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testToLogString
 */
fun Size.toLogString(): String {
    return if (this.isSpecified) {
        "${width}x$height"
    } else {
        "Unspecified"
    }
}

/**
 * Whether the [IntSize] is empty
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testIntSizeIsEmpty
 */
@Stable
internal fun IntSize.isEmpty(): Boolean = width <= 0 || height <= 0

/**
 * Get log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testIntSizeToLogString
 */
internal fun IntSize.toLogString(): String {
    return "${width}x$height"
}

/**
 * Convert [IntSize] to [SketchSize]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testIntSizeToSketchSize
 */
@Stable
fun IntSize.toSketchSize(): SketchSize = SketchSize(width, height)

/**
 * Convert [SketchSize] to [IntSize]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testSketchSizeToIntSize
 */
@Stable
fun SketchSize.toIntSize(): IntSize = IntSize(width, height)

/**
 * Convert [SketchSize] to [Size]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testSketchSizeToSize
 */
@Stable
fun SketchSize.toSize(): Size = Size(width.toFloat(), height.toFloat())

/**
 * Convert [Constraints] to [IntSize] or return null if it is zero or not bounded
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testConstraintsToIntSizeOrNull
 */
@Stable
internal fun Constraints.toIntSizeOrNull(): IntSize? = when {
    isZero -> null
    hasBoundedWidth && hasBoundedHeight -> IntSize(maxWidth, maxHeight)
    else -> null
}

/**
 * Convert [Constraints] to Resize
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testConstraintsToRequestSize
 */
@Stable
internal fun Constraints.toRequestSize(): IntSize {
    return IntSize(
        width = if (hasBoundedWidth) maxWidth else 0,
        height = if (hasBoundedHeight) maxHeight else 0
    )
}

/**
 * Find the leaf [Painter] of the [Painter]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testPainterFindLeafPainter
 */
fun Painter.findLeafPainter(): Painter {
    return when (val painter = this) {
        is CrossfadePainter -> painter.end?.findLeafPainter() ?: painter
        else -> painter
    }
}

/**
 * Find the deepest [Painter] of the [Painter]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testPainterFindLeafPainter
 */
fun Painter.findDeepestPainter(): Painter {
    return when (val painter = this) {
        is CrossfadePainter -> painter.end?.findDeepestPainter() ?: painter
        is PainterWrapper -> painter.painter.findDeepestPainter()
        else -> painter
    }
}

/**
 * Get the simple name of the [ColorSpace]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testSimpleName
 */
internal val ColorSpace.simpleName: String
    get() = when (this) {
        ColorSpaces.Srgb -> "Srgb"
        ColorSpaces.LinearSrgb -> "LinearSrgb"
        ColorSpaces.ExtendedSrgb -> "ExtendedSrgb"
        ColorSpaces.LinearExtendedSrgb -> "LinearExtendedSrgb"
        ColorSpaces.Bt709 -> "Bt709"
        ColorSpaces.Bt2020 -> "Bt2020"
        ColorSpaces.DciP3 -> "DciP3"
        ColorSpaces.DisplayP3 -> "DisplayP3"
        ColorSpaces.Ntsc1953 -> "Ntsc1953"
        ColorSpaces.SmpteC -> "SmpteC"
        ColorSpaces.AdobeRgb -> "AdobeRgb"
        ColorSpaces.ProPhotoRgb -> "ProPhotoRgb"
        ColorSpaces.Aces -> "Aces"
        ColorSpaces.Acescg -> "Acescg"
        ColorSpaces.CieXyz -> "CieXyz"
        ColorSpaces.CieLab -> "CieLab"
        ColorSpaces.Oklab -> "Oklab"
        else -> name
    }

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testAnyAsOrNull
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Returns a string representation of this Int value in the specified radix.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testToHexString
 */
internal fun Any.toHexString(): String = this.hashCode().toString(16)

/**
 * Convert [ImageBitmap] to log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testImageBitmapToLogString
 */
fun ImageBitmap.toLogString(): String =
    "ImageBitmap@${toHexString()}(${width}x${height},$config,${colorSpace.simpleName})"

/**
 * Create an [ImageBitmap] from the given [Bitmap]. Note this does not create a copy of the original
 * [Bitmap] and changes to it will modify the returned [ImageBitmap]
 */
expect fun Bitmap.asComposeImageBitmap(): ImageBitmap