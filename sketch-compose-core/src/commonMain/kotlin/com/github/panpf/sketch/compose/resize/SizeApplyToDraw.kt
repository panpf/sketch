package com.github.panpf.sketch.compose.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.compose.asPainter
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.compose.painter.AnimatablePainter
import com.github.panpf.sketch.compose.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.compose.painter.ResizePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.SizeApplyToDrawHelper
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size


object ComposeSizeApplyToDrawHelper : SizeApplyToDrawHelper {

    override val key: String = "ComposeSizeApplyToDrawHelper"

    override fun applySizeToDraw(request: ImageRequest, size: Size, image: Image): Image {
        return image
        // TODO 暂时不支持动图，改好后这里可以放开了
//        val scale = request.scaleDecider.get(imageSize = image.size, targetSize = size)
//        val painter = image.asPainter()
//        val composeSize =
//            androidx.compose.ui.geometry.Size(size.width.toFloat(), size.height.toFloat())
//        val resizePainter = if (painter is AnimatablePainter) {
//            ResizeAnimatablePainter(painter, composeSize, scale)
//        } else {
//            ResizePainter(painter, composeSize, scale)
//        }
//        return resizePainter.asSketchImage()
    }
}