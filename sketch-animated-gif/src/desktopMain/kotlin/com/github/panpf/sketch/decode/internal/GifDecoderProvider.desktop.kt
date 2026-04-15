package com.github.panpf.sketch.decode.internal

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.util.DecoderProvider

/**
 * Cooperate with [com.github.panpf.sketch.util.ComponentLoader] to achieve automatic registration [GifDecoder]
 *
 * @see com.github.panpf.sketch.animated.gif.desktop.test.decode.internal.GifDecoderProviderDesktopTest
 */
@Keep
actual class GifDecoderProvider : DecoderProvider {

    actual override fun factory(context: PlatformContext): Decoder.Factory {
        return SkiaGifDecoder.Factory()
    }
}