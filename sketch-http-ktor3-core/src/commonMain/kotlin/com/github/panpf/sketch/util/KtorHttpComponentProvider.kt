package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.KtorHttpUriFetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [KtorHttpUriFetcher]
 *
 * @see com.github.panpf.sketch.http.ktor3.jvm.test.util.KtorHttpComponentProviderJvmTest
 * @see com.github.panpf.sketch.http.ktor3.nonjvm.test.util.KtorHttpComponentProviderNonJvmTest
 */
expect class KtorHttpComponentProvider : ComponentProvider {
    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>?
    override fun addDecoders(context: PlatformContext): List<Decoder.Factory>?
    override fun addInterceptors(context: PlatformContext): List<Interceptor>?
    override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>?
    override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>?
    override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>?
}