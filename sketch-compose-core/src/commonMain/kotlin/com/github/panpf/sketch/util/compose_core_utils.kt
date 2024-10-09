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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.resize.Scale
import kotlin.math.roundToInt
import com.github.panpf.sketch.util.Size as SketchSize

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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testContentScaleToScale
 */
@Stable
internal fun ContentScale.toScale(): Scale {
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
 * Get the name of the [ContentScale]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testContentScaleName
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testContentScaleFitScale
 */
internal val ContentScale.fitScale: Boolean
    get() = this == ContentScale.Fit || this == Companion.Inside

/**
 * Convert [Size] to [IntSize] or return null if it is [Size.isUnspecified]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testSizeToIntSizeOrNull
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testToLogString
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testIntSizeIsEmpty
 */
@Stable
internal fun IntSize.isEmpty(): Boolean = width <= 0 || height <= 0

/**
 * Get log string
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testIntSizeToLogString
 */
internal fun IntSize.toLogString(): String {
    return "${width}x$height"
}

/**
 * Convert [IntSize] to [SketchSize]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testIntSizeToSketchSize
 */
@Stable
fun IntSize.toSketchSize(): SketchSize = SketchSize(width, height)

/**
 * Convert [SketchSize] to [IntSize]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testSketchSizeToIntSize
 */
@Stable
fun SketchSize.toIntSize(): IntSize = IntSize(width, height)

/**
 * Convert [SketchSize] to [Size]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testSketchSizeToSize
 */
@Stable
fun SketchSize.toSize(): Size = Size(width.toFloat(), height.toFloat())

/**
 * Convert [Constraints] to [IntSize] or return null if it is zero or not bounded
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testConstraintsToIntSizeOrNull
 */
@Stable
internal fun Constraints.toIntSizeOrNull(): IntSize? = when {
    isZero -> null
    hasBoundedWidth && hasBoundedHeight -> IntSize(maxWidth, maxHeight)
    else -> null
}

/**
 * Find the leaf [Painter] of the [Painter]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testPainterFindLeafPainter
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testPainterFindLeafPainter
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testSimpleName
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
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testAnyAsOrNull
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Returns a string representation of this Int value in the specified radix.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsTest.testToHexString
 */
internal fun Any.toHexString(): String = this.hashCode().toString(16)