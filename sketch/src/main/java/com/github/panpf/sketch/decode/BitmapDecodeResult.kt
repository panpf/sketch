package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.request.DataFrom
import java.util.LinkedList

data class BitmapDecodeResult constructor(
    val bitmap: Bitmap,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom,
    val transformedList: List<Transformed>? = null
) {

    fun new(bitmap: Bitmap, block: (Builder.() -> Unit)? = null): BitmapDecodeResult =
        Builder(bitmap, imageInfo, dataFrom, transformedList?.toMutableList()).apply {
            block?.invoke(this)
        }.build()

    class Builder(
        private val bitmap: Bitmap,
        private val info: ImageInfo,
        private val from: DataFrom,
        private var transformedList: MutableList<Transformed>? = null
    ) {

        fun addTransformed(transformed: Transformed): Builder = apply {
            if (this.transformedList?.find { it.key == transformed.key } == null) {
                this.transformedList = (this.transformedList ?: LinkedList()).apply {
                    add(transformed)
                }
            }
        }

        fun build(): BitmapDecodeResult = BitmapDecodeResult(
            bitmap,
            info,
            from,
            transformedList?.toList()
        )
    }
}