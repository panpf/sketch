package com.github.panpf.sketch.resize

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.Size

expect fun Image.sizeApplyToDraw(
    request: ImageRequest,
    resizeSize: Size?,
): Image