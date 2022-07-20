package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo

data class DisplayData constructor(
    val drawable: Drawable,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom,
    val transformedList: List<String>?,
) : ImageData

fun DrawableDecodeResult.toDisplayData(): DisplayData =
    DisplayData(drawable, imageInfo, dataFrom, transformedList)