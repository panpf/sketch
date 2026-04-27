package com.github.panpf.sketch.util

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.defaultAnimatedWebpDecoderFactory
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [ImageDecoderAnimatedWebpDecoder]
 *
 * @see com.github.panpf.sketch.animated.webp.android.test.util.AnimatedWebpComponentProviderAndroidTest
 */
@Keep
actual class AnimatedWebpComponentProvider : ComponentProvider {

    actual override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return null
    }

    actual override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return defaultAnimatedWebpDecoderFactory()?.let { listOf(it) }
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

    override fun toString(): String = "AnimatedWebpComponentProvider"
}