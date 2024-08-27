package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportSkiaAnimatedWebp
import com.github.panpf.sketch.decode.supportSkiaGif

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addComponents {
        supportSkiaGif()
        supportSkiaAnimatedWebp()
    }
}