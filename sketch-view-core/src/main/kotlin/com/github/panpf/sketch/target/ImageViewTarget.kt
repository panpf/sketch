/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.util.fitScale
import java.lang.ref.WeakReference

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewTarget constructor(
    view: ImageView
) : GenericViewTarget<ImageView>(view) {

    private val viewReference: WeakReference<ImageView> = WeakReference(view)

    override val view: ImageView?
        get() = viewReference.get()

    override var drawable: Drawable?
        get() = view?.drawable
        set(value) {
            view?.setImageDrawable(value)
        }

    override val fitScale: Boolean
        get() = view?.scaleType?.fitScale ?: true

    override fun getScaleDecider(): ScaleDecider? =
        view?.scaleType?.toScale()?.let { ScaleDecider(it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageViewTarget) return false
        if (view != other.view) return false
        return true
    }

    override fun hashCode(): Int {
        return view.hashCode()
    }

    override fun toString(): String {
        return "ImageViewTarget($view)"
    }

    private fun ScaleType.toScale(): Scale = when (this) {
        ScaleType.FIT_START -> Scale.START_CROP
        ScaleType.FIT_CENTER -> Scale.CENTER_CROP
        ScaleType.FIT_END -> Scale.END_CROP
        ScaleType.CENTER_INSIDE -> Scale.CENTER_CROP
        ScaleType.CENTER -> Scale.CENTER_CROP
        ScaleType.CENTER_CROP -> Scale.CENTER_CROP
        else -> Scale.FILL
    }
}