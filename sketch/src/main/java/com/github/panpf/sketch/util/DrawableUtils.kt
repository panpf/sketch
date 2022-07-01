package com.github.panpf.sketch.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import com.github.panpf.sketch.cache.BitmapPool


/**
 * Gets the last child Drawable
 */
internal fun LayerDrawable.getLastChildDrawable(): Drawable? {
    val layerCount = numberOfLayers.takeIf { it > 0 } ?: return null
    return getDrawable(layerCount - 1)
}

/**
 * Drawable into new Bitmap. Each time a new bitmap is drawn
 */
internal fun Drawable.toNewBitmap(bitmapPool: BitmapPool): Bitmap {
    val (oldLeft, oldTop, oldRight, oldBottom) = bounds
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)

    val bitmap: Bitmap = bitmapPool.getOrCreate(intrinsicWidth, intrinsicHeight, ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)

    setBounds(oldLeft, oldTop, oldRight, oldBottom) // restore bounds
    return bitmap
}