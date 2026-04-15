package com.github.panpf.sketch.animated.webp.android.test.decode.internal

import android.os.Build
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.internal.AnimatedWebpDecoderProvider
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AnimatedWebpDecoderProviderAndroidTest {

    @Test
    fun testFactory() {
        val context = getTestContext()
        val decoderProvider = AnimatedWebpDecoderProvider()
        val decoderFactory = decoderProvider.factory(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            assertEquals(
                expected = ImageDecoderAnimatedWebpDecoder.Factory(),
                actual = decoderFactory
            )
        } else {
            assertEquals(
                expected = null,
                actual = decoderFactory
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = AnimatedWebpDecoderProvider()
        val element11 = AnimatedWebpDecoderProvider()

        assertNotEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        val decoderProvider = AnimatedWebpDecoderProvider()
        assertTrue(
            actual = decoderProvider.toString().contains("AnimatedWebpDecoderProvider"),
            message = decoderProvider.toString()
        )
        assertTrue(
            actual = decoderProvider.toString().contains("@"),
            message = decoderProvider.toString()
        )
    }
}