package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

fun Image.sizeApplyToDraw(request: ImageRequest, size: Size?): Image {
    val sizeApplyToDrawHelper = request.sizeApplyToDraw
    if (sizeApplyToDrawHelper == null || size == null) return this
    return sizeApplyToDrawHelper.applySizeToDraw(request, size, this)
}

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface SizeApplyToDrawHelper {

    val key: String

    fun applySizeToDraw(request: ImageRequest, size: Size, image: Image): Image
}