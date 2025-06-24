package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.supportResourcesHttpUri
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.sample.util.ignoreFirst
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

expect fun initialApp(context: PlatformContext)

fun commonModule(context: PlatformContext): Module = module {
//    single { AppSettings(context) }
//    single { AppEvents() }
}

expect fun platformModule(context: PlatformContext): Module

fun newSketch(context: PlatformContext): Sketch {
    val appSettings: AppSettings = KoinPlatform.getKoin().get()
    return Sketch.Builder(context).apply {
        components {
            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()
            supportResourcesHttpUri(context)
        }

        networkParallelismLimited(appSettings.networkParallelismLimited.value)
        decodeParallelismLimited(appSettings.decodeParallelismLimited.value)

        // To be able to print the Sketch initialization log
        logger(level = appSettings.logLevel.value)

        platformSketchInitial(context)
    }.build().apply {
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            appSettings.logLevel.ignoreFirst().collect {
                logger.level = it
            }
        }
    }
}

expect fun Sketch.Builder.platformSketchInitial(context: PlatformContext)