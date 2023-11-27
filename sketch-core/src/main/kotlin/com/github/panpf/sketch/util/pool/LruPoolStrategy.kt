package com.github.panpf.sketch.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config

interface LruPoolStrategy {

    fun put(bitmap: Bitmap)

    fun get(width: Int, height: Int, config: Config): Bitmap?

    fun exist(width: Int, height: Int, config: Config): Boolean

    fun removeLast(): Bitmap?

    fun logBitmap(bitmap: Bitmap): String

    fun logBitmap(width: Int, height: Int, config: Config): String

    fun getSize(bitmap: Bitmap): Int
}