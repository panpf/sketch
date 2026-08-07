package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.sample.util.PexelsCompatibleInterceptor
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

const val appId = "com.github.panpf.sketch4.sample"

actual fun initialApp(context: PlatformContext, koinAppDeclaration: KoinAppDeclaration?) {
    startKoin {
        modules(commonModule(context))
        modules(platformModule(context))
        koinAppDeclaration?.invoke(this)
    }
}

actual fun platformModule(context: PlatformContext): Module = module {

}

actual fun Builder.platformSketchInitial(context: PlatformContext) {
    addComponents {
        add(PexelsCompatibleInterceptor())
    }
}