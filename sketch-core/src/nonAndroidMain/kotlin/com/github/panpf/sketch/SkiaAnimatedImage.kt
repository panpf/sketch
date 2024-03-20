package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.toLogString
import org.jetbrains.skia.Codec

@Stable
data class SkiaAnimatedImage(
    val codec: Codec,
    val repeatCount: Int? = null,
    val animationStartCallback: (() -> Unit)? = null,
    val animationEndCallback: (() -> Unit)? = null,
) : Image {

    override val width: Int = codec.width

    override val height: Int = codec.height

    override val byteCount: Long = 4L * width * height

    override val allocationByteCount: Long = byteCount

    override val shareable: Boolean = true

    override fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value? = null
    // Because repeatCount, animationStartCallback, and animationEndCallback cannot be cached in memory.
//    ): Value = SkiaAnimatedImageValue(this, extras)

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun getPixels(): IntArray? = null

    override fun toString(): String =
        "SkiaAnimatedImage(image=${codec.toLogString()}, shareable=$shareable)"
}

//class SkiaAnimatedImageValue(
//    animatedImage: SkiaAnimatedImage,
//    override val extras: Map<String, Any?> = emptyMap()
//) : Value {
//    override val image: Image = animatedImage
//    override val size: Long = animatedImage.byteCount
//    override fun checkValid(): Boolean = true
//}