package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.sample.data.api.giphy.GiphyApi
import com.github.panpf.sketch.sample.data.api.pexels.PexelsApi
import com.github.panpf.sketch.sample.ui.gallery.GiphyPhotoListViewModel
import com.github.panpf.sketch.sample.ui.gallery.LocalPhotoListViewModel
import com.github.panpf.sketch.sample.ui.gallery.PexelsPhotoListViewModel
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
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun initialApp(context: PlatformContext, koinAppDeclaration: KoinAppDeclaration? = null)

fun commonModule(context: PlatformContext): Module = module {
    single { AppSettings(context) }
    single { AppEvents() }
    single { newSketch(context, appSettings = get()) }
    single { newHttpClient() }
    single { PexelsApi(get()) }
    single { GiphyApi(get()) }

    viewModelOf(::PexelsPhotoListViewModel)
    viewModelOf(::GiphyPhotoListViewModel)
    viewModelOf(::LocalPhotoListViewModel)
    viewModel { DecoderTestViewModel(context) }
    viewModel { FetcherTestViewModel(context) }
    viewModel { ProgressIndicatorTestViewModel() }
}

expect fun platformModule(context: PlatformContext): Module

private fun newSketch(context: PlatformContext, appSettings: AppSettings): Sketch {
    return Sketch(context) {
        components {
            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()
        }

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
