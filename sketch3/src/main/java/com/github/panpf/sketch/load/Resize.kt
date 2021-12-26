/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.load

import android.widget.ImageView.ScaleType

data class Resize constructor(
    val width: Int,
    val height: Int,
    val scaleType: ScaleType = ScaleType.FIT_CENTER,
    val sizeMode: SizeMode = SizeMode.ASPECT_RATIO_SAME,
    val thumbnailMode: Boolean = false
) {

    fun newBuilder(
        configBlock: (Builder.() -> Unit)? = null
    ): Builder = Builder(this).apply {
        configBlock?.invoke(this)
    }

    fun new(
        configBlock: (Builder.() -> Unit)? = null
    ): Resize = Builder(this).apply {
        configBlock?.invoke(this)
    }.build()

    companion object {
        fun new(
            width: Int, height: Int,
            configBlock: (Builder.() -> Unit)? = null
        ): Resize = Builder(width, height).apply {
            configBlock?.invoke(this)
        }.build()
    }

    enum class SizeMode {
        /**
         * The size of the new image will not be larger than [Resize], but the aspect ratio will be the same
         */
        ASPECT_RATIO_SAME,

        /**
         * Even if the size of the original image is smaller than [Resize], you will get a [android.graphics.Bitmap] with the same size as [Resize]
         */
        SIZE_SAME
    }

    class Builder {

        private val width: Int
        private val height: Int
        private var scaleType: ScaleType
        private var sizeMode: SizeMode
        private var thumbnailMode: Boolean

        constructor(width: Int, height: Int) {
            this.width = width
            this.height = height
            this.scaleType = ScaleType.FIT_CENTER
            this.sizeMode = SizeMode.ASPECT_RATIO_SAME
            this.thumbnailMode = false
        }

        constructor(resize: Resize) {
            this.width = resize.width
            this.height = resize.height
            this.scaleType = resize.scaleType
            this.sizeMode = resize.sizeMode
            this.thumbnailMode = resize.thumbnailMode
        }

        fun scaleType(scaleType: ScaleType): Builder = apply {
            this.scaleType = scaleType
        }

        fun mode(sizeMode: SizeMode): Builder = apply {
            this.sizeMode = sizeMode
        }

        fun thumbnailMode(thumbnailMode: Boolean): Builder = apply {
            this.thumbnailMode = thumbnailMode
        }

        fun build(): Resize = Resize(width, height, scaleType, sizeMode, thumbnailMode)
    }
}