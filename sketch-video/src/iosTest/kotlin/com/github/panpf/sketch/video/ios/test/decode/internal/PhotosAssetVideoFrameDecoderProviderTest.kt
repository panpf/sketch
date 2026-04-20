package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.PhotosAssetVideoFrameDecoder
import com.github.panpf.sketch.decode.internal.PhotosAssetVideoFrameDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PhotosAssetVideoFrameDecoderProviderTest {

    @Test
    @Suppress("USELESS_IS_CHECK")
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = PhotosAssetVideoFrameDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        assertTrue(
            actual = decoderFactory is PhotosAssetVideoFrameDecoder.Factory,
            message = decoderFactory.toString()
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PhotosAssetVideoFrameDecoderProvider()
        val element11 = PhotosAssetVideoFrameDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = PhotosAssetVideoFrameDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("PhotosAssetVideoFrameDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}