package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.util.ComponentProvider
import kotlin.reflect.KClass

class DoNothingComponentProvider : ComponentProvider {

    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return listOf(DoNothingFetcher.Factory())
    }

    override fun addDecoders(context: PlatformContext): List<Decoder.Factory>? {
        return listOf(DoNothingDecoder.Factory())
    }

    override fun addInterceptors(context: PlatformContext): List<Interceptor>? {
        return listOf(DoNothingInterceptor())
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

    override fun toString(): String = "DoNothingComponentProvider"
}