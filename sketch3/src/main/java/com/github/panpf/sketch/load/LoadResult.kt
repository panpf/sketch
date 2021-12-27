package com.github.panpf.sketch.load

import android.graphics.Bitmap
import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.ImageResult

data class LoadResult(val bitmap: Bitmap, val info: ImageInfo, val from: DataFrom): ImageResult