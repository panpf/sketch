package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.getPixel

val SkiaBitmap.cornerA: Int
    get() = getPixel(0, 0)
val SkiaBitmap.cornerB: Int
    get() = getPixel(width - 1, 0)
val SkiaBitmap.cornerC: Int
    get() = getPixel(width - 1, height - 1)
val SkiaBitmap.cornerD: Int
    get() = getPixel(0, height - 1)

fun SkiaBitmap.corners(block: SkiaBitmap.() -> List<Int>): List<Int> {
    return block(this)
}

fun SkiaBitmap.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)

val SkiaBitmap.size: Size
    get() = Size(width, height)