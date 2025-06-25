package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.supportResourcesHttpUri
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

expect fun initialApp(context: PlatformContext)

fun commonModule(context: PlatformContext): Module = module {
    single { AppSettings(context) }
    single { AppEvents() }
    single { newSketch(context) }
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