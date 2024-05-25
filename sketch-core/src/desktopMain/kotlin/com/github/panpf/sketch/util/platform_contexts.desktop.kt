package com.github.panpf.sketch.util

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import net.harawata.appdirs.AppDirsFactory
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual fun PlatformContext.appCacheDirectory(): Path? {
    val appName = (getComposeResourcesPath() ?: getJarPath(Sketch::class.java))
        ?.md5()
        ?: throw UnsupportedOperationException(
            "Unable to generate application aliases to automatically initialize downloadCache and resultCache, " +
                    "please configure them manually. Documentation address 'https://github.com/panpf/sketch/blob/main/docs/wiki/getting_started.md'"
        )
    return requireNotNull(
        AppDirsFactory.getInstance().getUserCacheDir(
            /* appName = */ "SketchImageLoader${File.separator}${appName}",
            /* appVersion = */ null,
            /* appAuthor = */ null,
        )
    ) { "Failed to get the cache directory of the App" }.toPath()
}