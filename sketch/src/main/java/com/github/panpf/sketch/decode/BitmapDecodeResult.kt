package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.util.toInfoString
import java.util.LinkedList

/**
 * The result of [BitmapDecoder.decode]
 */
data class BitmapDecodeResult constructor(
    val bitmap: Bitmap,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom,
    val transformedList: List<String>? = null,
) {

    fun newResult(
        bitmap: Bitmap = this.bitmap,
        imageInfo: ImageInfo = this.imageInfo,
        dataFrom: DataFrom = this.dataFrom,
        block: (Builder.() -> Unit)? = null
    ): BitmapDecodeResult = Builder(
        bitmap = bitmap,
        imageInfo = imageInfo,
        dataFrom = dataFrom,
        transformedList = transformedList?.toMutableList(),
    ).apply {
        block?.invoke(this)
    }.build()

    override fun toString(): String =
        "BitmapDecodeResult(bitmap=${bitmap.toInfoString()}, " +
                "imageInfo=$imageInfo, " +
                "dataFrom=$dataFrom, " +
                "transformedList=$transformedList)"

    class Builder internal constructor(
        private val bitmap: Bitmap,
        private val imageInfo: ImageInfo,
        private val dataFrom: DataFrom,
        private var transformedList: MutableList<String>? = null,
    ) {

        fun addTransformed(transformed: String): Builder = apply {
            this.transformedList = (this.transformedList ?: LinkedList()).apply {
                add(transformed)
            }
        }

        fun build(): BitmapDecodeResult = BitmapDecodeResult(
            bitmap = bitmap,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList?.toList(),
        )
    }
}