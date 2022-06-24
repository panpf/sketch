package com.github.panpf.sketch.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.github.panpf.sketch.cache.BitmapPool


/**
 * Gets the last child Drawable
 */
internal fun LayerDrawable.getLastChildDrawable(): Drawable? {
    val layerCount = numberOfLayers.takeIf { it > 0 } ?: return null
    return getDrawable(layerCount - 1)
}

/**
 * Drawable into Bitmap. Each time a new bitmap is drawn
 */
internal fun Drawable.toBitmap(lowQuality: Boolean = false, bitmapPool: BitmapPool? = null): Bitmap {
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    @Suppress("DEPRECATION")
    val config = if (lowQuality) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888
    val bitmap: Bitmap = bitmapPool?.getOrCreate(intrinsicWidth, intrinsicHeight, config)
        ?: Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}