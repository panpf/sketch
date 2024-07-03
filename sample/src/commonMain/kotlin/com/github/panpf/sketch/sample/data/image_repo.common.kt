package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.images.ResourceImages

fun builtinImages(): List<ImageFile> {
    return ResourceImages.statics
        .asSequence()
        .plus(ResourceImages.anims)
        .plus(ResourceImages.longQMSHT)
        .plus(ResourceImages.clockExifs)
        .plus(ResourceImages.mp4)
        .toList()
}

expect suspend fun localImages(
    context: PlatformContext,
    startPosition: Int,
    pageSize: Int
): List<String>

expect suspend fun readImageInfoOrNull(
    context: PlatformContext,
    sketch: Sketch,
    uri: String,
): ImageInfo?