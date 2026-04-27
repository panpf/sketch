package com.github.panpf.sketch.util

import androidx.annotation.Keep
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.Interceptor
import kotlin.reflect.KClass

/**
 * Cooperate with [ComponentLoader] to achieve automatic registration [FFmpegVideoFrameDecoder]
 *
 * @see com.github.panpf.sketch.video.ffmpeg.test.util.FFmpegVideoComponentProviderTest
 */
@Keep
class FFmpegVideoComponentProvider : ComponentProvider {

    override fun addFetchers(context: PlatformContext): List<Fetcher.Factory>? {
        return null
    }

    override fun addDecoders(context: PlatformContext): List<Decoder.Factory> {
        return listOf(FFmpegVideoFrameDecoder.Factory())
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

    override fun toString(): String = "FFmpegVideoComponentProvider"
}