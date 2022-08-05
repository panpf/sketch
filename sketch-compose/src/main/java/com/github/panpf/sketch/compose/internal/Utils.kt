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
package com.github.panpf.sketch.compose.internal

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.resize.Scale
import com.google.accompanist.drawablepainter.DrawablePainter

/** Convert this [Drawable] into a [Painter] using Compose primitives if possible. */
internal fun Drawable.toPainter(filterQuality: FilterQuality) = when (this) {
    is BitmapDrawable -> BitmapPainter(bitmap.asImageBitmap(), filterQuality = filterQuality)
    is ColorDrawable -> ColorPainter(Color(color))
    else -> DrawablePainter(mutate())
}

//internal val Size.isPositive get() = width >= 0.5 && height >= 0.5

internal fun contentScale2ResizeScale(contentScale: ContentScale, alignment: Alignment): Scale =
    if (contentScale == ContentScale.FillBounds
        || contentScale == ContentScale.FillWidth
        || contentScale == ContentScale.FillHeight
    ) {
        Scale.FILL
    } else {
        when (alignment) {
            Alignment.Top, Alignment.TopStart, Alignment.TopEnd -> Scale.START_CROP
            Alignment.Center, Alignment.CenterStart, Alignment.CenterEnd -> Scale.CENTER_CROP
            Alignment.Bottom, Alignment.BottomStart, Alignment.BottomEnd -> Scale.END_CROP
            else -> Scale.CENTER_CROP
        }
    }