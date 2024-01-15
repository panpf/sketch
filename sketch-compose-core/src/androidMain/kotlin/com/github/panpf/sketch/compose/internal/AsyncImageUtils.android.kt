package com.github.panpf.sketch.compose.internal

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.painter.Painter
import com.google.accompanist.drawablepainter.DrawablePainter


/**
 * Convert this [Drawable] into a [Painter] using Compose primitives if possible.
 *
 * Very important, updateDisplayed() needs to set setIsDisplayed to keep SketchDrawable, SketchStateDrawable
 */
internal fun Drawable.toPainter() = DrawablePainter(mutate())
// Drawables from Sketch contain reference counting and therefore cannot be converted to the lower level Painter
//        when (this) {
//        is SketchDrawable -> DrawablePainter(mutate())
//        is SketchStateDrawable -> DrawablePainter(mutate())
//        is BitmapDrawable -> BitmapPainter(bitmap.asImageBitmap(), filterQuality = filterQuality)
//        is ColorDrawable -> ColorPainter(Color(color))
//        else -> DrawablePainter(mutate())
//    }