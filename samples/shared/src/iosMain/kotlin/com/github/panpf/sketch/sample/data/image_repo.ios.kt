package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext

actual suspend fun localImages(context: PlatformContext): List<String> =
    emptyList()   // TODO Read ios photo album