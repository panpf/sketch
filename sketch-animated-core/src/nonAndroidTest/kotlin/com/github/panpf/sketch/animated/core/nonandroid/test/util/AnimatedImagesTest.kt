package com.github.panpf.sketch.animated.core.nonandroid.test.util

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.test.utils.defaultColorType
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import kotlinx.coroutines.test.runTest
import okio.buffer
import okio.use
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedImagesTest {

    @Test
    fun testCodecToLogString() = runTest {
        val context = getTestContext()
        val bytes = ComposeResImageFiles.jpeg.toDataSource(context)
            .openSource().buffer()
            .use { it.readByteArray() }
        val data = Data.Companion.makeFromBytes(bytes)
        val codec = Codec.Companion.makeFromData(data)
        assertEquals(
            expected = "Codec@${
                codec.hashCode().toString(16)
            }(1291x1936,${defaultColorType.name},sRGB)",
            actual = codec.toLogString()
        )
    }
}