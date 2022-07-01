package com.github.panpf.sketch.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.cache.BitmapPool
import java.math.BigDecimal

internal fun Float.format(newScale: Int): Float =
    BigDecimal(toDouble()).setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()


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

internal fun Context?.getLifecycle(): Lifecycle? {
    var context: Context? = this
    while (true) {
        when (context) {
            is LifecycleOwner -> return context.lifecycle
            is ContextWrapper -> context = context.baseContext
            else -> return null
        }
    }
}