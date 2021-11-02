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
package com.github.panpf.sketch.display

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.SketchView

/**
 * The default image display, without any animation effects
 */
class DefaultImageDisplayer : ImageDisplayer {

    override val duration: Int
        get() = 0

    override val isAlwaysUse: Boolean
        get() = false

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        sketchView.clearAnimation()
        sketchView.setImageDrawable(newDrawable)
    }

    override fun toString(): String = "DefaultImageDisplayer"
}