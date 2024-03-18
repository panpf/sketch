package com.github.panpf.sketch.compose.cache

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.compose.ComposeBitmapImage

class ComposeBitmapValue(
    private val composeBitmapImage: ComposeBitmapImage,
    override val extras: Map<String, Any?>
) : Value {

    override val image: Image = composeBitmapImage

    override val size: Long = composeBitmapImage.byteCount

    override fun checkValid(): Boolean = composeBitmapImage.checkValid()
}