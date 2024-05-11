package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty

fun Image.resizeOnDraw(request: ImageRequest, size: Size?): Image {
    if (size?.isNotEmpty == true && request.resizeOnDraw == true) {
        val resizeOnDrawHelper = request.target?.getResizeOnDrawHelper()
        if (resizeOnDrawHelper != null) {
            return resizeOnDrawHelper.resize(request, size, this)
        }
    }
    return this
}

/**
 * Use ResizeDrawable or ResizePainter to wrap an Image to resize it while drawing
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface ResizeOnDrawHelper {

    val key: String

    fun resize(request: ImageRequest, size: Size, image: Image): Image
}