/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.compose.state

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.ColorFetcher
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.state.ResColor
import com.github.panpf.sketch.state.StateImage

@Composable
fun rememberColorPainterStateImage(colorFetcher: ColorFetcher): ColorFetcherPainterStateImage =
    remember(colorFetcher) { ColorFetcherPainterStateImageImpl(colorFetcher) }

@Composable
fun rememberColorPainterStateImage(intColor: IntColor): ColorFetcherPainterStateImage =
    remember(intColor) { ColorFetcherPainterStateImageImpl(intColor) }

@Composable
fun rememberColorPainterStateImage(@ColorRes colorRes: Int): ColorFetcherPainterStateImage =
    remember(colorRes) { ColorFetcherPainterStateImageImpl(ResColor(colorRes)) }

interface ColorFetcherPainterStateImage : StateImage {
    val colorFetcher: ColorFetcher
}

private class ColorFetcherPainterStateImageImpl(
    override val colorFetcher: ColorFetcher
) : ColorFetcherPainterStateImage {

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        return ColorPainter(Color(colorFetcher.getColor(request.context))).asSketchImage()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ColorFetcherPainterStateImage) return false
        if (colorFetcher != other.colorFetcher) return false
        return true
    }

    override fun hashCode(): Int {
        return colorFetcher.hashCode()
    }

    override fun toString(): String {
        return "ColorFetcherPainterStateImage($colorFetcher)"
    }
}