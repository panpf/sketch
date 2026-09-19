package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch.Builder
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.sample.util.PexelsCompatibleInterceptor
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

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
        val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
        val cachesDirectory = (paths.firstOrNull() as? String)?.toPath()
        val appCacheDirectory = cachesDirectory?.resolve(AppInfos.SAMPLE_APP_NAME)
        DiskCache.Options(
            appCacheDirectory = appCacheDirectory
        )
    }
    resultCacheOptions {
        val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
        val cachesDirectory = (paths.firstOrNull() as? String)?.toPath()
        val appCacheDirectory = cachesDirectory?.resolve(AppInfos.SAMPLE_APP_NAME)
        DiskCache.Options(
            appCacheDirectory = appCacheDirectory
        )
    }

    addComponents {
        add(PexelsCompatibleInterceptor())
    }
}