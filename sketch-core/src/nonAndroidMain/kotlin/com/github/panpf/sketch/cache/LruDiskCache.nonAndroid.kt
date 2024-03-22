package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import okio.Path

actual fun checkDiskCacheDirectory(context: PlatformContext, directory: Path): Path = directory