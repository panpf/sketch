package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

fun Image.resizeOnDraw(request: ImageRequest, size: Size?): Image {
    val resizeOnDrawHelper = request.resizeOnDrawHelper
    if (resizeOnDrawHelper == null || size == null) return this
    return resizeOnDrawHelper.resize(request, size, this)
}

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface ResizeOnDrawHelper {

    val key: String

    fun resize(request: ImageRequest, size: Size, image: Image): Image
}