package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

fun Image.sizeApplyToDraw(request: ImageRequest, size: Size?): Image {
    val sizeApplyToDrawHelper = request.sizeApplyToDraw
    if (sizeApplyToDrawHelper == null || size == null) return this
    return sizeApplyToDrawHelper.applySizeToDraw(request, size, this)
}

interface SizeApplyToDrawHelper {

    val key: String

    fun applySizeToDraw(request: ImageRequest, size: Size, image: Image): Image
}