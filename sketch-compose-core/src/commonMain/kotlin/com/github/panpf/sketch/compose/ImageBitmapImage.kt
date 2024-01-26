package com.github.panpf.sketch.compose

import androidx.compose.ui.graphics.ImageBitmap
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.compose.painter.toLogString
import com.github.panpf.sketch.request.internal.RequestContext


fun ImageBitmap.asSketchImage(shareable: Boolean = true): Image {
    return ImageBitmapImage(this, shareable)
}

data class ImageBitmapImage(
    val imageBitmap: ImageBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = imageBitmap.width

    override val height: Int = imageBitmap.height

    override val byteCount: Int = 4 * width * height  // TODO check

    override val allocationByteCount: Int = 4 * width * height

    override fun cacheValue(requestContext: RequestContext, extras: Map<String, Any?>): Value =
        ImageBitmapValue(this, extras)

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer? = null

    override fun toString(): String {
        return "ImageBitmapImage(imageBitmap=${imageBitmap.toLogString()}, shareable=$shareable)"
    }
}

class ImageBitmapValue(
    val imageBitmapImage: ImageBitmapImage,
    override val extras: Map<String, Any?>
) : Value {

    override val image: Image = imageBitmapImage

    override val size: Int = imageBitmapImage.byteCount

    override fun checkValid(): Boolean = imageBitmapImage.checkValid()
}