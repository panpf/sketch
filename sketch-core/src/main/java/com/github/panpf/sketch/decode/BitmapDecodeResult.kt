package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.ImageInfo

data class BitmapDecodeResult(
    val bitmap: Bitmap,
    val info: ImageInfo,
    val from: DataFrom
    // todo 增加 处理标记列表，可以知道都经过了哪些处理，比如 maxSize，resize，纠正方向等
)