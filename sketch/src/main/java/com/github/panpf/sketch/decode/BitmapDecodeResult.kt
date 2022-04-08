package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.util.toInfoString
import java.util.LinkedList

data class BitmapDecodeResult constructor(
    val bitmap: Bitmap,
    override val imageInfo: ImageInfo,
    override val exifOrientation: Int,
    override val dataFrom: DataFrom,
    override val transformedList: List<Transformed>? = null
) : DecodeResult {

    fun new(bitmap: Bitmap, block: (Builder.() -> Unit)? = null): BitmapDecodeResult =
        Builder(
            bitmap = bitmap,
            imageInfo = imageInfo,
            exifOrientation = exifOrientation,
            dataFrom = dataFrom,
            transformedList = transformedList?.toMutableList()
        ).apply {
            block?.invoke(this)
        }.build()

    override fun toString(): String =
        "BitmapDecodeResult(bitmap=${bitmap.toInfoString()}, " +
                "imageInfo=$imageInfo, " +
                "exifOrientation=${exifOrientationName(exifOrientation)}, " +
                "dataFrom=$dataFrom, " +
                "transformedList=$transformedList)"

    class Builder(
        private val bitmap: Bitmap,
        private var imageInfo: ImageInfo,
        private val exifOrientation: Int,
        private val dataFrom: DataFrom,
        private var transformedList: MutableList<Transformed>? = null
    ) {

        fun imageInfo(imageInfo: ImageInfo): Builder = apply {
            this.imageInfo = imageInfo
        }

        fun addTransformed(transformed: Transformed): Builder = apply {
            if (this.transformedList?.find { it.key == transformed.key } == null) {
                this.transformedList = (this.transformedList ?: LinkedList()).apply {
                    add(transformed)
                }
            }
        }

        fun build(): BitmapDecodeResult = BitmapDecodeResult(
            bitmap = bitmap,
            imageInfo = imageInfo,
            exifOrientation = exifOrientation,
            dataFrom = dataFrom,
            transformedList = transformedList?.toList()
        )
    }
}