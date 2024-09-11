package com.github.panpf.sketch.core.nonandroid.test.decode

import com.github.panpf.sketch.decode.fromName
import com.github.panpf.sketch.decode.name
import com.github.panpf.sketch.decode.values
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorSpaceTest {

    @Test
    fun testName() {
        assertEquals("sRGB", ColorSpace.sRGB.name())
        assertEquals("sRGBLinear", ColorSpace.sRGBLinear.name())
        assertEquals("displayP3", ColorSpace.displayP3.name())
    }

    @Test
    fun testFromName() {
        assertEquals(ColorSpace.sRGB, ColorSpace.fromName("sRGB"))
        assertEquals(ColorSpace.sRGBLinear, ColorSpace.fromName("sRGBLinear"))
        assertEquals(ColorSpace.displayP3, ColorSpace.fromName("displayP3"))
    }

    @Test
    fun testValues() {
        assertEquals(
            listOf(ColorSpace.sRGB, ColorSpace.sRGBLinear, ColorSpace.displayP3),
            ColorSpace.values()
        )
    }
}