package com.github.panpf.sketch.util

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [com.github.panpf.sketch.util.ComponentLoader] to achieve automatic registration [SkiaGifDecoder]
 *
 * @see com.github.panpf.sketch.animated.gif.desktop.test.util.GifComponentProviderDesktopTest
 */
@Keep
actual class GifComponentProvider : ComponentProvider {

    actual override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return null
    }

    actual override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return listOf(SkiaGifDecoder.Factory())
    }

    actual override fun addInterceptors(context: PlatformContext): List<Interceptor>? {
        return null
    }

    actual override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>? {
        return null
    }

    actual override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>? {
        return null
    }

    actual override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>? {
        return null
    }

    override fun toString(): String = "GifComponentProvider"
}