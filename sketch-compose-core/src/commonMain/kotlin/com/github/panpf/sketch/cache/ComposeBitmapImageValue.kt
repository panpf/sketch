package com.github.panpf.sketch.cache

import com.github.panpf.sketch.ComposeBitmapImage
import com.github.panpf.sketch.Image

class ComposeBitmapImageValue(
    private val composeBitmapImage: ComposeBitmapImage,
    override val extras: Map<String, Any?>?
) : MemoryCache.Value {

    override val image: Image = composeBitmapImage

    override val size: Long = composeBitmapImage.byteCount

    override fun checkValid(): Boolean = composeBitmapImage.checkValid()
}