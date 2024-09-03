package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.internal.BitmapFactoryDecodeHelper
import com.github.panpf.sketch.decode.internal.DecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataSource

actual fun createDecodeHelper(request: ImageRequest, dataSource: DataSource): DecodeHelper {
    return BitmapFactoryDecodeHelper(request, dataSource)
}