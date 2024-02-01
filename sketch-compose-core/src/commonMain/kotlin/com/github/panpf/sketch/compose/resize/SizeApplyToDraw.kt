package com.github.panpf.sketch.compose.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.compose.asPainter
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.compose.painter.resize
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.SizeApplyToDrawHelper
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size
import androidx.compose.ui.geometry.Size as ComposeSize


object ComposeSizeApplyToDrawHelper : SizeApplyToDrawHelper {

    override val key: String = "ComposeSizeApplyToDrawHelper"

    override fun applySizeToDraw(request: ImageRequest, size: Size, image: Image): Image {
        val scale = request.scaleDecider.get(imageSize = image.size, targetSize = size)
        val painter = image.asPainter()
        val composeSize = ComposeSize(size.width.toFloat(), size.height.toFloat())
        val resizePainter = painter.resize(composeSize, scale)
        return resizePainter.asSketchImage()
    }
}