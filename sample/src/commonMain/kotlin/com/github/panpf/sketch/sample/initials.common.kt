package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.supportResourcesHttpUri
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi
import com.github.panpf.sketch.sample.ui.test.DecoderTestViewModel
import com.github.panpf.sketch.sample.ui.test.FetcherTestViewModel
import com.github.panpf.sketch.sample.ui.test.ProgressIndicatorTestViewModel
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

expect fun initialApp(context: PlatformContext)

fun commonModule(context: PlatformContext): Module = module {
    single { AppSettings(context) }
    single { AppEvents() }
    single { newSketch(context) }
    single { newHttpClient() }
    single { PexelsApi(get()) }
    single { GiphyApi(get()) }

    viewModel { DecoderTestViewModel(context) }
    viewModel { FetcherTestViewModel(context) }
    viewModel { ProgressIndicatorTestViewModel() }
}

expect fun platformModule(context: PlatformContext): Module

private fun newSketch(context: PlatformContext): Sketch {
    return Sketch(context) {
        components {
            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()
            supportResourcesHttpUri(context)
        }

        val appSettings: AppSettings = KoinPlatform.getKoin().get()
        networkParallelismLimited(appSettings.networkParallelismLimited.value)
        decodeParallelismLimited(appSettings.decodeParallelismLimited.value)

        // To be able to print the Sketch initialization log
        val logger1 = Logger(appSettings.logLevel.value).apply {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                appSettings.logLevel.ignoreFirst().collect {
                    level = it
                }
            }
        }
        logger(logger1)

        platformSketchInitial(context)
    }
}

expect fun Sketch.Builder.platformSketchInitial(context: PlatformContext)

private fun newHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
}
