package com.github.panpf.sketch.util

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [AppIconUriFetcher]
 *
 * @see com.github.panpf.sketch.extensions.appicon.test.util.AppIconComponentProviderTest
 */
@Keep
class AppIconComponentProvider : ComponentProvider {

    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory> {
        return listOf(AppIconUriFetcher.Factory())
    }

    override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return null
    }

    override fun addInterceptors(context: PlatformContext): List<Interceptor>? {
        return null
    }

    override fun disabledFetchers(context: PlatformContext): List<KClass<out Fetcher.Factory>>? {
        return null
    }

    override fun disabledDecoders(context: PlatformContext): List<KClass<out Decoder.Factory>>? {
        return null
    }

    override fun disabledInterceptors(context: PlatformContext): List<KClass<out Interceptor>>? {
        return null
    }

    override fun toString(): String = "AppIconComponentProvider"
}