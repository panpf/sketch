package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed

data class DisplayData constructor(
    val drawable: Drawable,
    val imageInfo: ImageInfo,
    @ExifOrientation val imageExifOrientation: Int,
    val dataFrom: DataFrom,
    val transformedList: List<Transformed>?,
) : ImageData

fun DrawableDecodeResult.toDisplayData(): DisplayData =
    DisplayData(drawable, imageInfo, imageExifOrientation, dataFrom, transformedList)