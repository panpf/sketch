package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.Transformed
import com.github.panpf.sketch.util.BitmapInfo

class SketchCrossfadeDrawable<END>(
    start: Drawable?,
    private val _end: END,
    durationMillis: Int = DEFAULT_DURATION,
    fadeStart: Boolean = true,
    preferExactIntrinsicSize: Boolean = false,
) : CrossfadeDrawable(start, _end, durationMillis, fadeStart, preferExactIntrinsicSize),
    SketchDrawable where END : Drawable, END : SketchDrawable {

    override val requestKey: String
        get() = _end.requestKey

    override val requestUri: String
        get() = _end.requestUri

    override val imageInfo: ImageInfo
        get() = _end.imageInfo

    override val imageExifOrientation: Int
        get() = _end.imageExifOrientation

    override val dataFrom: DataFrom
        get() = _end.dataFrom

    override val transformedList: List<Transformed>?
        get() = _end.transformedList

    override val bitmapInfo: BitmapInfo
        get() = _end.bitmapInfo
}