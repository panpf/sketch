/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    /**
     * Store the transformation history of the Bitmap
     */
    val transformedList: List<String>?,
    /**
     * Store some additional information for consumer use,
     * You can add information here during decoding, transformation, interceptor, etc.
     */
    val extras: Map<String, String>?,
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
        extras = extras?.toMutableMap(),
    ).apply {
        block?.invoke(this)
    }.build()

    override fun toString(): String =
        "BitmapDecodeResult(bitmap=${bitmap.toInfoString()}, " +
                "imageInfo=$imageInfo, " +
                "dataFrom=$dataFrom, " +
                "transformedList=$transformedList, " +
                "extras=$extras)"

    class Builder internal constructor(
        private val bitmap: Bitmap,
        private val imageInfo: ImageInfo,
        private val dataFrom: DataFrom,
        private var transformedList: MutableList<String>? = null,
        private var extras: MutableMap<String, String>? = null,
    ) {

        fun addTransformed(transformed: String): Builder = apply {
            this.transformedList = (this.transformedList ?: LinkedList()).apply {
                add(transformed)
            }
        }

        fun addExtras(key: String, value: String): Builder = apply {
            this.extras = (this.extras ?: mutableMapOf()).apply {
                put(key, value)
            }
        }

        fun build(): BitmapDecodeResult = BitmapDecodeResult(
            bitmap = bitmap,
            imageInfo = imageInfo,
            dataFrom = dataFrom,
            transformedList = transformedList?.toList(),
            extras = extras?.toMap(),
        )
    }
}