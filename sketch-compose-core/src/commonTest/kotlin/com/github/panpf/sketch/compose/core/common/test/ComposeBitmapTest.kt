package com.github.panpf.sketch.compose.core.common.test

import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.toLogString
import com.github.panpf.sketch.util.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeBitmapTest {

    @Test
    fun testComposeBitmap() {
        assertEquals(
            expected = androidx.compose.ui.graphics.ImageBitmap::class,
            actual = ComposeBitmap::class
        )
    }

    @Test
    fun testComposeBitmapToLogString() {
        val composeBitmap = createBitmap(101, 202).toComposeBitmap()
        assertEquals(
            expected = "ComposeBitmap@${composeBitmap.toHexString()}(101x202,Argb8888,Srgb)",
            actual = composeBitmap.toLogString()
        )
    }
}