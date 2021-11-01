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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
import com.github.panpf.sketch.SketchView
import java.util.*

/**
 * 由大到小图片显示器
 */
class ZoomOutImageDisplayer @JvmOverloads constructor(
    val fromX: Float = 1.5f,
    val fromY: Float = 1.5f,
    val interpolator: Interpolator? = AccelerateDecelerateInterpolator(),
    override val duration: Int = ImageDisplayer.DEFAULT_ANIMATION_DURATION,
    override val isAlwaysUse: Boolean = false
) : ImageDisplayer {

    constructor(fromX: Float, fromY: Float, interpolator: Interpolator?, alwaysUse: Boolean) : this(
        fromX,
        fromY,
        interpolator,
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    )

    constructor(fromX: Float, fromY: Float, alwaysUse: Boolean) : this(
        fromX,
        fromY,
        AccelerateDecelerateInterpolator(),
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    ) {
    }

    constructor(interpolator: Interpolator?, alwaysUse: Boolean) : this(
        1.5f,
        1.5f,
        interpolator,
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    ) {
    }

    constructor(interpolator: Interpolator?) : this(
        1.5f,
        1.5f,
        interpolator,
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        false
    ) {
    }

    constructor(duration: Int, alwaysUse: Boolean) : this(
        1.5f,
        1.5f,
        AccelerateDecelerateInterpolator(),
        duration,
        alwaysUse
    ) {
    }

    constructor(duration: Int) : this(
        1.5f,
        1.5f,
        AccelerateDecelerateInterpolator(),
        duration,
        false
    ) {
    }

    constructor(alwaysUse: Boolean) : this(
        1.5f,
        1.5f,
        AccelerateDecelerateInterpolator(),
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    ) {
    }

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        sketchView.apply {
            clearAnimation()
            setImageDrawable(newDrawable)
            startAnimation(ScaleAnimation(
                this@ZoomOutImageDisplayer.fromX,
                1.0f,
                this@ZoomOutImageDisplayer.fromY,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                interpolator = this@ZoomOutImageDisplayer.interpolator
                duration = this@ZoomOutImageDisplayer.duration.toLong()
            })
        }
    }

    override fun toString(): String =
        "%s(duration=%d,fromX=%s,fromY=%s,interpolator=%s,alwaysUse=%s)".format(
            Locale.US,
            "ZoomOutImageDisplayer",
            duration,
            fromX,
            fromY,
            interpolator?.javaClass?.simpleName,
            isAlwaysUse
        )
}