package com.github.panpf.sketch.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.GifAnimatedSkiaDecoder
import com.github.panpf.sketch.decode.WebpAnimatedSkiaDecoder
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.util.Logger
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
                addDecoder(GifAnimatedSkiaDecoder.Factory())
                addDecoder(WebpAnimatedSkiaDecoder.Factory())
            }
            logger(Logger(level = Logger.level(appSettings.logLevel.value)))
        }.build().apply {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                appSettings.logLevel.collect {
                    logger.level = Logger.level(it)
                }
            }
        }
    }
}