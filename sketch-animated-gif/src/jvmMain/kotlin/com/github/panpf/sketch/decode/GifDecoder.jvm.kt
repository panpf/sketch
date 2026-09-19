@file:JvmName("GifDecoder_desktopKt")   // For binary compatibility
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry

/**
 * Adds gif animated image support
 *
 * @see com.github.panpf.sketch.animated.gif.jvm.test.decode.GifDecoderJvmTest.testSupportGif
 */
actual fun ComponentRegistry.Builder.supportGif(): ComponentRegistry.Builder = apply {
    add(SkiaGifDecoder.Factory())
}