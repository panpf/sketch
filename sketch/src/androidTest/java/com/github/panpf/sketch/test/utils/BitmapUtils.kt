package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap


val Bitmap.cornerA: Int
    get() = getPixel(0, 0)
val Bitmap.cornerB: Int
    get() = getPixel(width - 1, 0)
val Bitmap.cornerC: Int
    get() = getPixel(width - 1, height - 1)
val Bitmap.cornerD: Int
    get() = getPixel(0, height - 1)

fun Bitmap.corners(block: Bitmap.() -> List<Int>): List<Int> {
    return block(this)
}

fun Bitmap.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)