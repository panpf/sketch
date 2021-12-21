package com.github.panpf.sketch3.common.cache.disk

import com.github.panpf.sketch3.common.SketchException
import java.io.File

class InstallDiskCacheException(cause: Throwable, val cacheDir: File) : SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}