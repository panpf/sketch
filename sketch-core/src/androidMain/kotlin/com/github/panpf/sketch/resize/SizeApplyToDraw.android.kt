package com.github.panpf.sketch.resize

import android.graphics.drawable.Animatable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size

class AndroidSizeApplyToDrawHelper : SizeApplyToDrawHelper {

    override val key: String = "AndroidSizeApplyToDrawHelper"

    override fun applySizeToDraw(request: ImageRequest, size: Size, image: Image): Image {
        val scale = request.scaleDecider.get(imageSize = image.size, targetSize = size)
        val drawable = image.asDrawable()
        val resizeDrawable = if (drawable is Animatable) {
            ResizeAnimatableDrawable(drawable, size, scale)
        } else {
            ResizeDrawable(drawable, size, scale)
        }
        return resizeDrawable.asSketchImage()
    }
}