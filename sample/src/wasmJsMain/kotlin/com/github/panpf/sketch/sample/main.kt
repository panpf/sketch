package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportSkiaAnimatedWebp
import com.github.panpf.sketch.decode.supportSkiaGif
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.supportComposeResources
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initialSketch()
    CanvasBasedWindow("SketchSample-WASM") {
        App()
    }
}

private fun initialSketch() {
    val context = PlatformContext.INSTANCE
    val appSettings = context.appSettings
    SingletonSketch.setSafe {
        Sketch.Builder(context).apply {
            components {
                supportSvg()
                supportSkiaGif()
                supportSkiaAnimatedWebp()
                supportComposeResources()
            }
            // To be able to print the Sketch initialization log
            logger(level = appSettings.logLevel.value)
            networkParallelismLimited(appSettings.networkParallelismLimited.value)
            decodeParallelismLimited(appSettings.decodeParallelismLimited.value)
        }.build().apply {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                appSettings.logLevel.collect {
                    logger.level = it
                }
            }
        }
    }
}