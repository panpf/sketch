package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.sample.util.PexelsCompatibleInterceptor
import com.github.panpf.sketch.util.AppDirs
import okio.Path.Companion.toPath
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

actual fun Builder.platformSketchInitial(context: PlatformContext) {
    downloadCacheOptions {
        DiskCache.Options(
            appCacheDirectory = AppDirs.getCacheDir(AppInfos.SAMPLE_APP_NAME).toPath()
        )
    }
    resultCacheOptions {
        DiskCache.Options(
            appCacheDirectory = AppDirs.getCacheDir(AppInfos.SAMPLE_APP_NAME).toPath()
        )
    }

    addComponents {
        add(PexelsCompatibleInterceptor())
    }
}