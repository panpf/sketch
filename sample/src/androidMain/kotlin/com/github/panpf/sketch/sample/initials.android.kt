package com.github.panpf.sketch.sample

import android.os.Build
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecoderProvider
import com.github.panpf.sketch.decode.internal.GifDecoderProvider
import com.github.panpf.sketch.decode.internal.KoralGifDecoderProvider
import com.github.panpf.sketch.decode.internal.VideoFrameDecoderProvider
import com.github.panpf.sketch.decode.supportFFmpegVideoFrame
import com.github.panpf.sketch.decode.supportImageDecoderGif
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.OkHttpHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addIgnoreFetcherProvider(
        KtorHttpUriFetcherProvider::class,
        OkHttpHttpUriFetcherProvider::class,
        HurlHttpUriFetcherProvider::class
    )

    addIgnoreDecoderProvider(
        VideoFrameDecoderProvider::class,
        FFmpegVideoFrameDecoderProvider::class
    )

    addIgnoreDecoderProvider(
        KoralGifDecoderProvider::class,
        GifDecoderProvider::class
    )

    addComponents {
        val appSettings = context.appSettings
        when (val httpClient = appSettings.httpClient.value) {
            "Ktor" -> supportKtorHttpUri()
            "OkHttp" -> supportOkHttpHttpUri()
            "HttpURLConnection" -> supportHurlHttpUri()
            else -> throw IllegalArgumentException("Unknown httpClient: $httpClient")
        }

        when (appSettings.videoFrameDecoder.value) {
            "FFmpeg" -> supportFFmpegVideoFrame()
            "AndroidBuiltIn" -> supportVideoFrame()
            else -> throw IllegalArgumentException("Unknown videoFrameDecoder: ${appSettings.videoFrameDecoder.value}")
        }

        when (appSettings.gifDecoder.value) {
            "KoralGif" -> supportKoralGif()
            "Movie" -> supportMovieGif()
            "Movie+ImageDecoder" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) supportImageDecoderGif() else supportMovieGif()
            else -> throw IllegalArgumentException("Unknown animatedDecoder: ${appSettings.gifDecoder.value}")
        }
    }
}