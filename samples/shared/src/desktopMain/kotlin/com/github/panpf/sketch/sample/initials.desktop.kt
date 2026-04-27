package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.supportHurlHttpUri
import com.github.panpf.sketch.fetch.supportKtorHttpUri
import com.github.panpf.sketch.fetch.supportOkHttpHttpUri
import com.github.panpf.sketch.sample.ui.util.PexelsCompatibleInterceptor
import com.github.panpf.sketch.util.HurlHttpComponentProvider
import com.github.panpf.sketch.util.KtorHttpComponentProvider
import com.github.panpf.sketch.util.OkHttpHttpComponentProvider
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val appId = "com.github.panpf.sketch4.sample"

actual fun initialApp(context: PlatformContext, koinAppDeclaration: KoinAppDeclaration?) {
    startKoin {
        modules(commonModule(context))
        modules(platformModule(context))
        koinAppDeclaration?.invoke(this)
    }
}

actual fun platformModule(context: PlatformContext): Module = module {

}

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addIgnoredComponentProvider(
        KtorHttpComponentProvider::class,
        OkHttpHttpComponentProvider::class,
        HurlHttpComponentProvider::class
    )

    val appSettings: AppSettings = KoinPlatform.getKoin().get()
    addComponents {
        when (val httpClient = appSettings.httpClient.value) {
            "Ktor" -> supportKtorHttpUri()
            "OkHttp" -> supportOkHttpHttpUri()
            "HttpURLConnection" -> supportHurlHttpUri()
            else -> throw IllegalArgumentException("Unknown httpClient: $httpClient")
        }

        add(PexelsCompatibleInterceptor())
    }
}