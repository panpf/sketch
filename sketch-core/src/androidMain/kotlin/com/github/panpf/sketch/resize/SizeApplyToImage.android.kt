package com.github.panpf.sketch.resize

import android.graphics.drawable.Animatable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.internal.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.Size

actual fun Image.sizeApplyToDraw(
    request: ImageRequest,
    size: Size?,
): Image {
    return if (request.sizeApplyToDraw && size != null) {
        val scale = request.scaleDecider.get(imageSize = this.size, targetSize = size)
        val drawable = this.asDrawable()
        if (drawable is Animatable) {
            ResizeAnimatableDrawable(drawable, size, scale)
        } else {
            ResizeDrawable(drawable, size, scale)
        }.asSketchImage()
    } else {
        this
    }
}