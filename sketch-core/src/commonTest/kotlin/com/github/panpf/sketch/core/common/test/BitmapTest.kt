package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.byteCount
import com.github.panpf.sketch.height
import com.github.panpf.sketch.isImmutable
import com.github.panpf.sketch.isMutable
import com.github.panpf.sketch.test.utils.create565Bitmap
import com.github.panpf.sketch.test.utils.createARGBBitmap
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.getImmutableBitmap
import com.github.panpf.sketch.test.utils.getMutableBitmap
import com.github.panpf.sketch.width
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapTest {

    @Test
    fun testWidth() {
        assertEquals(expected = 100, actual = createBitmap(100, 200).width)
        assertEquals(expected = 200, actual = createBitmap(200, 100).width)
    }

    @Test
    fun testHeight() {
        assertEquals(expected = 200, actual = createBitmap(100, 200).height)
        assertEquals(expected = 100, actual = createBitmap(200, 100).height)
    }

    @Test
    fun testByteCount() {
        assertEquals(expected = 80000, actual = createARGBBitmap(100, 200).byteCount)
        assertEquals(expected = 40000, actual = create565Bitmap(200, 100).byteCount)
    }

    @Test
    fun testIsMutable() {
        assertTrue(getMutableBitmap().isMutable)
        assertFalse(getImmutableBitmap().isMutable)
    }

    @Test
    fun testIsImmutable() {
        assertFalse(getMutableBitmap().isImmutable)
        assertTrue(getImmutableBitmap().isImmutable)
    }
}