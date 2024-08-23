package com.github.panpf.zoomimage.sketch

import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.cache.MemoryCache

/**
 * [SkiaBitmapImage] MemoryCache.Value
 *
 * @see com.github.panpf.zoomimage.core.sketch.nonandroid.test.SkiaBitmapImageValueTest
 */
class SkiaBitmapImageValue(
    override val image: SkiaBitmapImage,
    override val extras: Map<String, Any?>? = null,
) : MemoryCache.Value {

    override val size: Long = image.byteCount

    override fun checkValid(): Boolean {
        return image.checkValid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SkiaBitmapImageValue
        if (image != other.image) return false
        return extras == other.extras
    }

    override fun hashCode(): Int {
        var result = image.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

    override fun toString(): String {
        return "SkiaBitmapImageValue(image=${image}, extras=$extras)"
    }
}