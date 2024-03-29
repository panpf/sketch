/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.compose.internal

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.compose.AsyncImageState.Companion.DefaultTransform
import com.github.panpf.sketch.compose.PainterState
import com.github.panpf.sketch.compose.PainterState.Empty
import com.github.panpf.sketch.compose.PainterState.Error
import com.github.panpf.sketch.compose.PainterState.Loading
import com.github.panpf.sketch.compose.PainterState.Success
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.resize.Scale
import kotlin.math.roundToInt
import com.github.panpf.sketch.util.Size as SketchSize

@Stable
fun transformOf(
    placeholder: Painter?,
    error: Painter?,
    uriEmpty: Painter?,
): (PainterState) -> PainterState {
    return if (placeholder != null || error != null || uriEmpty != null) {
        { state ->
            when (state) {
                is Loading -> {
                    if (placeholder != null) state.copy(painter = placeholder) else state
                }

                is Error -> if (state.result.throwable is UriInvalidException) {
                    if (uriEmpty != null) state.copy(painter = uriEmpty) else state
                } else {
                    if (error != null) state.copy(painter = error) else state
                }

                else -> state
            }
        }
    } else {
        DefaultTransform
    }
}

@Stable
fun onPainterStateOf(
    onLoading: ((Loading) -> Unit)?,
    onSuccess: ((Success) -> Unit)?,
    onError: ((Error) -> Unit)?,
): ((PainterState) -> Unit)? {
    return if (onLoading != null || onSuccess != null || onError != null) {
        { state ->
            when (state) {
                is Loading -> onLoading?.invoke(state)
                is Success -> onSuccess?.invoke(state)
                is Error -> onError?.invoke(state)
                is Empty -> {}
            }
        }
    } else {
        null
    }
}

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
        else -> "Unknown ContentScale: $this"
    }

internal fun Constraints.constrainWidth(width: Float) =
    width.coerceIn(minWidth.toFloat(), maxWidth.toFloat())

internal fun Constraints.constrainHeight(height: Float) =
    height.coerceIn(minHeight.toFloat(), maxHeight.toFloat())

internal inline fun Float.takeOrElse(block: () -> Float) = if (isFinite()) this else block()

internal fun Size.toIntSize() = IntSize(width.roundToInt(), height.roundToInt())

internal fun Size.toIntSizeOrNull() = when {
    isUnspecified -> null

    width >= 0.5 && height >= 0.5 && width.isFinite() && height.isFinite() -> IntSize(
        width.roundToInt(),
        height.roundToInt()
    )

    else -> null
}

@Stable
internal fun IntSize.isEmpty(): Boolean = width <= 0 || height <= 0

@Stable
internal fun IntSize.toSketchSize(): SketchSize = SketchSize(width, height)

@Stable
internal fun Constraints.toIntSizeOrNull(): IntSize? = when {
    isZero -> null
    hasBoundedWidth && hasBoundedHeight -> IntSize(maxWidth, maxHeight)
    else -> null
}

@Suppress("REDUNDANT_ELSE_IN_WHEN")
internal val PainterState.name: String
    get() = when (this) {
        is Loading -> "Loading"
        is Success -> "Success"
        is Error -> "Error"
        is Empty -> "Empty"
        else -> "Unknown PainterState: $this"
    }

internal fun Painter.findLeafChildPainter(): Painter? {
    return when (val painter = this) {
        is CrossfadePainter -> {
            painter.end?.findLeafChildPainter()
        }

        else -> painter
    }
}