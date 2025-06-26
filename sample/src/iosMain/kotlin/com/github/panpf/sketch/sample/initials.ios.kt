package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.ui.gallery.GiphyPhotoListViewModel
import com.github.panpf.sketch.sample.ui.gallery.LocalPhotoListViewModel
import com.github.panpf.sketch.sample.ui.gallery.PexelsPhotoListViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual fun initialApp(context: PlatformContext) {
    startKoin {
        modules(commonModule(context))
        modules(platformModule(context))
    }
}

actual fun platformModule(context: PlatformContext): Module = module {
    viewModelOf(::PexelsPhotoListViewModel)
    viewModelOf(::LocalPhotoListViewModel)
    viewModelOf(::GiphyPhotoListViewModel)
}

actual fun Sketch.Builder.platformSketchInitial(context: PlatformContext) {

}