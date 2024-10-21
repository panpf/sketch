package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.supportComposeResources
import com.github.panpf.sketch.request.supportPauseLoadWhenScrolling
import com.github.panpf.sketch.request.supportSaveCellularTraffic
import com.github.panpf.sketch.sample.util.ignoreFirst
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun newSketch(context: PlatformContext): Sketch {
    val appSettings = context.appSettings
    return Sketch.Builder(context).apply {
        componentLoaderEnabled(false)
        components {
            supportComposeResources()

            supportSaveCellularTraffic()
            supportPauseLoadWhenScrolling()

            supportSvg()
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