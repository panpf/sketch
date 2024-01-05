/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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

import android.graphics.drawable.Drawable
import android.widget.ImageView
import java.lang.ref.WeakReference

/**
 * A [Target] that handles setting images on an [ImageView].
 */
open class ImageViewTarget constructor(
    view: ImageView,
) : GenericViewTarget<ImageView>(view) {

    private val viewReference: WeakReference<ImageView> = WeakReference(view)

    override val view: ImageView?
        get() = viewReference.get()

    override var drawable: Drawable?
        get() = view?.drawable
        set(value) {
            view?.setImageDrawable(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ImageViewTarget
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