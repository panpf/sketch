package com.github.panpf.sketch.animated.core.nonandroid.test.util

import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.test.utils.defaultColorType
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.toLogString
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedImagesTest {

    @Test
    fun testCodecToLogString() = runTest {
        val context = getTestContext()
        val bytes = ComposeResImageFiles.jpeg.toDataSource(context).toByteArray()
        val data = Data.makeFromBytes(bytes)
        val codec = Codec.makeFromData(data)
        assertEquals(
            expected = "Codec@${
                codec.hashCode().toString(16)
            }(1291x1936,${defaultColorType.name},sRGB)",
            actual = codec.toLogString()
        )
    }
}