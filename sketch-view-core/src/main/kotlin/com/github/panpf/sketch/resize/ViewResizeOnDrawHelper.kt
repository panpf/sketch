package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asDrawableOrThrow
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.resize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size

object ViewResizeOnDrawHelper : ResizeOnDrawHelper {

    override val key: String = "ViewResizeOnDrawHelper"

    override fun resize(request: ImageRequest, size: Size, image: Image): Image {
        val scale = request.scaleDecider.get(imageSize = image.size, targetSize = size)
        val drawable = image.asDrawableOrThrow()
        val resizeDrawable = drawable.resize(size, scale)
        return resizeDrawable.asSketchImage()
    }
}