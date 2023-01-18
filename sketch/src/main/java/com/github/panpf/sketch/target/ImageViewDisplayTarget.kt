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
package com.github.panpf.sketch.target

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.util.iterateSketchCountBitmapDrawable

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewDisplayTarget(override val view: ImageView) :
    GenericViewDisplayTarget<ImageView>() {

    /**
     * @see [ViewTargetRequestDelegate.onViewDetachedFromWindow]
     */
    override var drawable: Drawable?
        get() = view.drawable
        set(value) {
            val oldDrawable = view.drawable
            value?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(true, "ImageView")
            }
            if (value is CrossfadeDrawable) {
                value.start?.let { start ->
                    require(start.callback != null) { "start.callback is null. set before" }
                }
                require(value.end?.callback != null) { "end.callback is null. set before" }
            }
            view.setImageDrawable(value)
            if (value is CrossfadeDrawable) {
                value.start?.let { start ->
                    if (start === oldDrawable) {
                        require(start.callback == null) { "start.callback is not null. set after" }
                        start.callback = value
                    } else {
                        require(start.callback != null) { "start.callback is null. set after" }
                    }
                }
                require(value.end?.callback != null) { "end.callback is null. set after" }
            }
            oldDrawable?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(false, "ImageView")
            }
            if (oldDrawable is Animatable) {
                oldDrawable.stop()
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageViewDisplayTarget
        if (view != other.view) return false
        return true
    }

    override fun hashCode(): Int {
        return view.hashCode()
    }

    override fun toString(): String {
        return "ImageViewDisplayTarget($view)"
    }
}
