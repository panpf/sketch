package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.ImageData

data class LoadData constructor(
    val bitmap: Bitmap,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom
): ImageData

fun BitmapDecodeResult.toLoadData(): LoadData = LoadData(bitmap, imageInfo, dataFrom)