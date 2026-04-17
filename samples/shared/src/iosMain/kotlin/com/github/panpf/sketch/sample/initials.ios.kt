package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

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

}