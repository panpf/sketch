package com.github.panpf.sketch.sample.data

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.images.ComposeResImageFiles

actual suspend fun buildPlatformBuiltinPhotoList(sketch: Sketch): List<String> {
    // TODO Use multiple ImageFiles so you can directly test various Fetchers on the Local page.
    return ComposeResImageFiles.statics
        .asSequence()
        .plus(ComposeResImageFiles.anims)
        .plus(ComposeResImageFiles.numbersGif)
        .plus(ComposeResImageFiles.longQMSHT)
        .plus(ComposeResImageFiles.longCOMIC)
        .plus(ComposeResImageFiles.clockExifs)
        .plus(ComposeResImageFiles.videos)
        .map { it.uri }
        .toList()
}