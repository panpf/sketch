package com.github.panpf.sketch.sample.ui.components.zoomimage.core

import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.cache.MemoryCache

class BufferedImageValue(
    override val image: BufferedImageImage,
    override val extras: Map<String, Any?> = emptyMap()
) : MemoryCache.Value {

    override val size: Int = image.byteCount

    override fun checkValid(): Boolean {
        return image.checkValid()
    }
}