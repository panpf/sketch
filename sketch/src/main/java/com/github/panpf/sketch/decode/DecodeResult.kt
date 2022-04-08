package com.github.panpf.sketch.decode

import com.github.panpf.sketch.datasource.DataFrom

interface DecodeResult {
    val imageInfo: ImageInfo
    val exifOrientation: Int
    val dataFrom: DataFrom
    val transformedList: List<Transformed>?
}