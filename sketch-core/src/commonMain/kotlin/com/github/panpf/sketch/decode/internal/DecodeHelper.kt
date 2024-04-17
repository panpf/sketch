@file:Suppress("UnnecessaryVariable")

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Rect
import okio.Closeable

interface DecodeHelper : Closeable {

    val imageInfo: ImageInfo

    val supportRegion: Boolean

    fun decode(sampleSize: Int): Image

    fun decodeRegion(region: Rect, sampleSize: Int): Image
}