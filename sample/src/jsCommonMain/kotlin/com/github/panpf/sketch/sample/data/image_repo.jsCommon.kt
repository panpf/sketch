package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext

actual suspend fun localImages(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String> = emptyList()