package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportSkiaAnimatedWebp
import com.github.panpf.sketch.decode.supportSkiaGif
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.KtorStack
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleRequestInterceptor

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    val appSettings = context.appSettings
    val httpStack = when (appSettings.httpClient.value) {
        "Ktor" -> KtorStack()
        "OkHttp" -> OkHttpStack.Builder().build()
        "HttpURLConnection" -> HurlStack.Builder().build()
        else -> throw IllegalArgumentException("Unknown httpClient: ${appSettings.httpClient.value}")
    }
    httpStack(httpStack)
    addComponents {
        supportSkiaGif()
        supportSkiaAnimatedWebp()

        addRequestInterceptor(PexelsCompatibleRequestInterceptor())
    }
}