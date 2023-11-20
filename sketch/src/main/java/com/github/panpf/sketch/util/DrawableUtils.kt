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
package com.github.panpf.sketch.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.WorkerThread
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.getOrCreate
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable

/**
 * Find the last child [Drawable] from the specified Drawable
 */
fun Drawable.findLeafChildDrawable(): Drawable? {
    return when (val drawable = this) {
        is CrossfadeDrawable -> {
            drawable.end?.findLeafChildDrawable()
        }

        is LayerDrawable -> {
            val layerCount = drawable.numberOfLayers
            if (layerCount > 0) {
                drawable.getDrawable(layerCount - 1).findLeafChildDrawable()
            } else {
                null
            }
        }

        else -> drawable
    }
}

/**
 * Find the last child [Drawable] from the specified Drawable
 */
@Deprecated(
    message = "Please use findLeafChildDrawable()",
    replaceWith = ReplaceWith(expression = "findLeafChildDrawable")
)
fun Drawable.getLastChildDrawable(): Drawable? {
    return findLeafChildDrawable()
}

/**
 * Drawable into new Bitmap. Each time a new bitmap is drawn
 */
@WorkerThread
internal fun Drawable.toNewBitmap(
    bitmapPool: BitmapPool,
    disallowReuseBitmap: Boolean,
    preferredConfig: Bitmap.Config? = null,
    targetSize: Size? = null
): Bitmap {
    val (oldLeft, oldTop, oldRight, oldBottom) = bounds
    val targetWidth = targetSize?.width ?: intrinsicWidth
    val targetHeight = targetSize?.height ?: intrinsicHeight
    setBounds(0, 0, targetWidth, targetHeight)

    val config = preferredConfig ?: ARGB_8888
    val bitmap: Bitmap = bitmapPool.getOrCreate(
        width = targetWidth,
        height = targetHeight,
        config = config,
        disallowReuseBitmap = disallowReuseBitmap,
        caller = "toNewBitmap"
    )
    val canvas = Canvas(bitmap)
    draw(canvas)

    setBounds(oldLeft, oldTop, oldRight, oldBottom) // restore bounds
    return bitmap
}