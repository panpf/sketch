package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

actual fun Image.sizeApplyToDraw(
    request: ImageRequest,
    size: Size?,
): Image {
    return this
}