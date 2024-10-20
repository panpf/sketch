package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.decode.supportGif
import com.github.panpf.sketch.fetch.supportKtorHttpUri

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {
    addComponents {
        supportGif()
        supportAnimatedWebp()
        supportKtorHttpUri()
    }
}