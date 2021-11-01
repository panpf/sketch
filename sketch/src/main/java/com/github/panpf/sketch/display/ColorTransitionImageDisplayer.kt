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

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.github.panpf.sketch.SketchView
import java.util.*

/**
 * 颜色渐入图片显示器
 */
class ColorTransitionImageDisplayer @JvmOverloads constructor(
    val color: Int,
    override val duration: Int = ImageDisplayer.DEFAULT_ANIMATION_DURATION,
    override val isAlwaysUse: Boolean = false
) : ImageDisplayer {

    constructor(color: Int, alwaysUse: Boolean)
            : this(color, ImageDisplayer.DEFAULT_ANIMATION_DURATION, alwaysUse)

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        val transitionDrawable = TransitionDrawable(arrayOf(ColorDrawable(color), newDrawable))
        sketchView.clearAnimation()
        sketchView.setImageDrawable(transitionDrawable)
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(duration)
    }

    override fun toString(): String = "%s(duration=%d,color=%d,alwaysUse=%s)"
        .format(Locale.US, "ColorTransitionImageDisplayer", duration, color, isAlwaysUse)
}