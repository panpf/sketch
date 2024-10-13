package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageTest {

    @Test
    fun testSketchImage() {
        assertEquals(
            expected = com.github.panpf.sketch.Image::class,
            actual = com.github.panpf.sketch.SketchImage::class
        )
    }

    @Test
    fun testSize() {
        assertEquals(
            expected = Size(101, 202),
            actual = FakeImage(Size(101, 202)).size
        )
        assertEquals(
            expected = Size(202, 101),
            actual = FakeImage(Size(202, 101)).size
        )
    }
}