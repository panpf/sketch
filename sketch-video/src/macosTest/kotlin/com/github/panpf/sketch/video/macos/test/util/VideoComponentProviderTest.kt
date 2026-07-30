package com.github.panpf.sketch.video.macos.test.util

import com.github.panpf.sketch.decode.FileVideoFrameDecoder
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.VideoComponentProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class VideoComponentProviderTest {

    @Test
    fun testAddAndDisabled() {
        val context = getTestContext()
        val componentProvider = VideoComponentProvider()
        assertNull(componentProvider.addFetchers(context))
        assertEquals(
            expected = listOf(FileVideoFrameDecoder.Factory()),
            actual = componentProvider.addDecoders(context),
        )
        assertNull(componentProvider.addInterceptors(context))
        assertNull(componentProvider.disabledFetchers(context))
        assertNull(componentProvider.disabledDecoders(context))
        assertNull(componentProvider.disabledInterceptors(context))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = VideoComponentProvider()
        val element2 = VideoComponentProvider()

        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "VideoComponentProvider",
            actual = VideoComponentProvider().toString(),
        )
    }
}
