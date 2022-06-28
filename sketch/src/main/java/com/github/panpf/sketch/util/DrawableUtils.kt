package com.github.panpf.sketch.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
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
 * Drawable into Bitmap
 */
internal fun Drawable.toBitmap(
    lowQuality: Boolean = false,
    bitmapPool: BitmapPool? = null
): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap ?: throw IllegalArgumentException("bitmap is null")
    }

    val (oldLeft, oldTop, oldRight, oldBottom) = bounds
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)

    @Suppress("DEPRECATION")
    val config = if (lowQuality) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
    val bitmap: Bitmap = bitmapPool?.getOrCreate(intrinsicWidth, intrinsicHeight, config)
        ?: Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    draw(canvas)

    setBounds(oldLeft, oldTop, oldRight, oldBottom) // restore bounds
    return bitmap
}