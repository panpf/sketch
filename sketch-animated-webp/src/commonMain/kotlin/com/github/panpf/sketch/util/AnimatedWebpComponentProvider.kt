package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration AnimatedWebpDecoder
 *
 * @see com.github.panpf.sketch.animated.webp.android.test.util.AnimatedWebpComponentProviderAndroidTest
 * @see com.github.panpf.sketch.animated.webp.ios.test.util.AnimatedWebpComponentProviderIosTest
 * @see com.github.panpf.sketch.animated.webp.desktop.test.util.AnimatedWebpComponentProviderDesktopTest
 * @see com.github.panpf.sketch.animated.webp.jscommon.test.util.AnimatedWebpComponentProviderJsCommonTest
 */
expect class AnimatedWebpComponentProvider : ComponentProvider {
    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>?
    override fun addDecoders(context: PlatformContext): List<Decoder.Factory>?
    override fun addInterceptors(context: PlatformContext): List<Interceptor>?
    override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>?
    override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>?
    override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>?
}