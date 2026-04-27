package com.github.panpf.sketch.sample

import android.os.Build
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportFFmpegVideoFrame
import com.github.panpf.sketch.decode.supportImageDecoderGif
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri
import com.github.panpf.sketch.sample.ui.test.LocalVideoListViewModel
import com.github.panpf.sketch.sample.util.PenfeizhouAnimatedWebpDecoder
import com.github.panpf.sketch.util.FFmpegVideoComponentProvider
import com.github.panpf.sketch.util.GifComponentProvider
import com.github.panpf.sketch.util.HurlHttpComponentProvider
import com.github.panpf.sketch.util.KoralGifComponentProvider
import com.github.panpf.sketch.util.KtorHttpComponentProvider
import com.github.panpf.sketch.util.OkHttpHttpComponentProvider
import com.github.panpf.sketch.util.VideoComponentProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

actual fun initialApp(context: PlatformContext, koinAppDeclaration: KoinAppDeclaration?) {
    startKoin {
        androidContext(context)
        modules(commonModule(context))
        modules(platformModule(context))
        koinAppDeclaration?.invoke(this)
    }
}

actual fun platformModule(context: PlatformContext): Module = module {
    viewModelOf(::LocalVideoListViewModel)
}

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addIgnoredComponentProvider(
        KtorHttpComponentProvider::class,
        OkHttpHttpComponentProvider::class,
        HurlHttpComponentProvider::class,
        VideoComponentProvider::class,
        FFmpegVideoComponentProvider::class,
        KoralGifComponentProvider::class,
        GifComponentProvider::class
    )

    val appSettings: AppSettings = KoinPlatform.getKoin().get()
    addComponents {
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            add(PenfeizhouAnimatedWebpDecoder.Factory())
        }
    }
}