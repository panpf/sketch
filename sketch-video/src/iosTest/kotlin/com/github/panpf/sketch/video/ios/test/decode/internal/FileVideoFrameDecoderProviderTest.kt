package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.FileVideoFrameDecoder
import com.github.panpf.sketch.decode.internal.FileVideoFrameDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FileVideoFrameDecoderProviderTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = FileVideoFrameDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(
            actual = decoderFactory is FileVideoFrameDecoder.Factory,
            message = decoderFactory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = FileVideoFrameDecoderProvider()
        val element11 = FileVideoFrameDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = FileVideoFrameDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("FileVideoFrameDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}