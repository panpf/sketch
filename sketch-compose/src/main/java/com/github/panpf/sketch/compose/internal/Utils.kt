package com.github.panpf.sketch.compose.internal

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import com.google.accompanist.drawablepainter.DrawablePainter

/** Convert this [Drawable] into a [Painter] using Compose primitives if possible. */
internal fun Drawable.toPainter(filterQuality: FilterQuality) = when (this) {
    is BitmapDrawable -> BitmapPainter(bitmap.asImageBitmap(), filterQuality = filterQuality)
    is ColorDrawable -> ColorPainter(Color(color))
    else -> DrawablePainter(mutate())
}

internal val Size.isPositive get() = width >= 0.5 && height >= 0.5