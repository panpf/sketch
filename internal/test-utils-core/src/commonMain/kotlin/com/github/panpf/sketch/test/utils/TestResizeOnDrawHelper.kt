package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.ResizeOnDrawHelper
import com.github.panpf.sketch.util.Size

class TestResizeOnDrawHelper : ResizeOnDrawHelper {
    override fun resize(request: ImageRequest, size: Size, image: Image): Image {
        return TestResizeOnDrawImage(image)
    }

    override val key: String = "TestResizeOnDrawHelper"
}