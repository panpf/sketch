package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry

/**
 * Adds gif animated image support
 *
 * @see com.github.panpf.sketch.animated.gif.ios.test.decode.GifDecoderIosTest.testSupportGif
 */
actual fun ComponentRegistry.Builder.supportGif(): ComponentRegistry.Builder = apply {
    add(SkiaGifDecoder.Factory())
}