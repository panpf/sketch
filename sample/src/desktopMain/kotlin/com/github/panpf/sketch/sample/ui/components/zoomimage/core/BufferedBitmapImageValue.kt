package com.github.panpf.sketch.sample.ui.components.zoomimage.core

import com.github.panpf.sketch.cache.MemoryCache

class BufferedBitmapImageValue(
    override val image: BufferedBitmapImage,
    override val extras: Map<String, Any?> = emptyMap()
) : MemoryCache.Value {

    override val size: Long = image.byteCount

    override fun checkValid(): Boolean {
        return image.checkValid()
    }
}