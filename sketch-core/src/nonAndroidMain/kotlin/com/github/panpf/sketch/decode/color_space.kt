package com.github.panpf.sketch.decode

import org.jetbrains.skia.ColorSpace

/**
 * Get the name of the color space
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.ColorSpaceTest.testName
 */
fun ColorSpace.name(): String {
    return when (this) {
        ColorSpace.sRGB -> "sRGB"
        ColorSpace.sRGBLinear -> "sRGBLinear"
        ColorSpace.displayP3 -> "displayP3"
        else -> throw IllegalArgumentException("Unsupported ColorSpace: $this")
    }
}

/**
 * Get the color space by name
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.ColorSpaceTest.testFromName
 */
fun ColorSpace.Companion.fromName(name: String): ColorSpace {
    return when (name) {
        "sRGB" -> sRGB
        "sRGBLinear" -> sRGBLinear
        "displayP3" -> displayP3
        else -> throw IllegalArgumentException("Unsupported ColorSpace name: $name")
    }
}

/**
 * Get all color spaces
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.ColorSpaceTest.testValues
 */
fun ColorSpace.Companion.values(): List<ColorSpace> {
    return listOf(sRGB, sRGBLinear, displayP3)
}