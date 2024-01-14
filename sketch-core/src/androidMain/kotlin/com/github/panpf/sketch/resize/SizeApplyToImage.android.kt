package com.github.panpf.sketch.resize

import android.graphics.drawable.Animatable
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.drawable.internal.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.ResizeDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

actual fun Image.sizeApplyToDraw(
    request: ImageRequest,
    resizeSize: Size?,
): Image {
    return if (request.sizeApplyToDraw && resizeSize != null) {
        val scale = request.resizeScaleDecider.get(
            imageWidth = width,
            imageHeight = height,
            resizeWidth = resizeSize.width,
            resizeHeight = resizeSize.height
        )
        val drawable = this.asDrawable()
        if (drawable is Animatable) {
            ResizeAnimatableDrawable(drawable, resizeSize, scale)
        } else {
            ResizeDrawable(drawable, resizeSize, scale)
        }.asSketchImage()
    } else {
        this
    }
}