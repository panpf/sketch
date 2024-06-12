package com.github.panpf.sketch.resize

import androidx.compose.ui.geometry.Size as ComposeSize
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.painter.resize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size


object ComposeResizeOnDrawHelper : ResizeOnDrawHelper {

    override val key: String = "ComposeResizeOnDrawHelper"

    override fun resize(request: ImageRequest, size: Size, image: Image): Image {
        val scale = request.scaleDecider.get(imageSize = image.size, targetSize = size)
        val painter = image.asPainter()
        val composeSize = ComposeSize(size.width.toFloat(), size.height.toFloat())
        val resizePainter = painter.resize(composeSize, scale)
        return resizePainter.asSketchImage()
    }
}