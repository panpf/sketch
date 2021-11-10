package com.github.panpf.sketch.cache

import com.github.panpf.sketch.SketchException
import java.io.File

class InstallDiskCacheException(cause: Throwable, val cacheDir: File) : SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}