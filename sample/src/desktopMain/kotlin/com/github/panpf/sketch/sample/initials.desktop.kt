package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportGif
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleRequestInterceptor

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addComponents {
        when (val httpClient = context.appSettings.httpClient.value) {
            "Ktor" -> supportKtorHttpUri()
            "OkHttp" -> supportOkHttpHttpUri()
            "HttpURLConnection" -> supportHurlHttpUri()
            else -> throw IllegalArgumentException("Unknown httpClient: $httpClient")
        }

        supportGif()
        supportAnimatedWebp()

        addRequestInterceptor(PexelsCompatibleRequestInterceptor())
    }
}