package com.github.panpf.sketch.core.common.test

import com.github.panpf.sketch.asBitmap
import com.github.panpf.sketch.asBitmapOrNull
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.test.utils.FakeImage
import com.github.panpf.sketch.test.utils.createBitmap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class BitmapImageTest {

    @Test
    fun testImageAsBitmapOrNull() {
        val bitmap = createBitmap(100, 200)

        assertSame(
            expected = bitmap,
            actual = bitmap.asImage().asBitmapOrNull()
        )

        assertEquals(
            expected = null,
            actual = FakeImage(100, 200).asBitmapOrNull()
        )
    }

    @Test
    fun testImageAsBitmap() {
        val bitmap = createBitmap(100, 200)

        assertSame(
            expected = bitmap,
            actual = bitmap.asImage().asBitmap()
        )

        assertFailsWith(IllegalArgumentException::class) {
            FakeImage(100, 200).asBitmap()
        }
    }
}