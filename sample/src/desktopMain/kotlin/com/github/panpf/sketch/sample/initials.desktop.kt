package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.internal.HurlHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.KtorHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.internal.OkHttpHttpUriFetcherProvider
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleRequestInterceptor
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

actual fun initialApp(context: PlatformContext) {
    startKoin {
        modules(commonModule(context))
        modules(platformModule(context))
    }
}

actual fun platformModule(context: PlatformContext): Module = module {

}

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addIgnoreFetcherProvider(
        KtorHttpUriFetcherProvider::class,
        OkHttpHttpUriFetcherProvider::class,
        HurlHttpUriFetcherProvider::class
    )

    val appSettings: AppSettings = KoinPlatform.getKoin().get()
    addComponents {
        when (val httpClient = appSettings.httpClient.value) {
            "Ktor" -> supportKtorHttpUri()
            "OkHttp" -> supportOkHttpHttpUri()
            "HttpURLConnection" -> supportHurlHttpUri()
            else -> throw IllegalArgumentException("Unknown httpClient: $httpClient")
        }

        addRequestInterceptor(PexelsCompatibleRequestInterceptor())
    }
}