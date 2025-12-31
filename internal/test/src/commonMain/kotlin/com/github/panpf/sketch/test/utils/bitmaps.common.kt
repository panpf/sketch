package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.height
import com.github.panpf.sketch.util.readIntPixel
import com.github.panpf.sketch.width


expect fun createBitmap(width: Int, height: Int): Bitmap

expect fun createARGBBitmap(width: Int, height: Int): Bitmap

expect fun create565Bitmap(width: Int, height: Int): Bitmap

expect fun getMutableBitmap(): Bitmap

expect fun getImmutableBitmap(): Bitmap

val Bitmap.cornerA: Int
    get() = readIntPixel(0, 0)
val Bitmap.cornerB: Int
    get() = readIntPixel(width - 1, 0)
val Bitmap.cornerC: Int
    get() = readIntPixel(width - 1, height - 1)
val Bitmap.cornerD: Int
    get() = readIntPixel(0, height - 1)

fun Bitmap.corners(block: Bitmap.() -> List<Int>): List<Int> {
    return block(this)
}

fun Bitmap.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)

expect fun Bitmap.toPreviewBitmap(): Any

expect val defaultColorType: ColorType