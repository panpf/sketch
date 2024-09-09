package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.MemoryCache

expect fun createImage(width: Int, height: Int): Image

expect fun createCacheValue(image: Image, extras: Map<String, Any?>): MemoryCache.Value

val Image.cornerA: Int
    get() = getPixel(0, 0)
val Image.cornerB: Int
    get() = getPixel(width - 1, 0)
val Image.cornerC: Int
    get() = getPixel(width - 1, height - 1)
val Image.cornerD: Int
    get() = getPixel(0, height - 1)

fun Image.corners(block: Image.() -> List<Int>): List<Int> {
    return block(this)
}

fun Image.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)

expect fun Image.hasAlphaPixels(): Boolean

/**
 * Returns the Color at the specified location.
 */
expect fun Image.getPixel(x: Int, y: Int): Int