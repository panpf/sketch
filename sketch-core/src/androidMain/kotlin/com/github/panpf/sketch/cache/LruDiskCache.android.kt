package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.util.fileNameCompatibilityMultiProcess
import okio.Path
import okio.Path.Companion.toOkioPath

actual fun checkDiskCacheDirectory(context: PlatformContext, directory: Path): Path {
    return fileNameCompatibilityMultiProcess(context, directory.toFile()).toOkioPath()
}