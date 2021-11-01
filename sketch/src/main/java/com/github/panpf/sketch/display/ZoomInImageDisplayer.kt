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
 * 由小到大图片显示器
 */
class ZoomInImageDisplayer @JvmOverloads constructor(
    val fromX: Float = DEFAULT_FROM,
    val fromY: Float = DEFAULT_FROM,
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
    )

    constructor(interpolator: Interpolator?, alwaysUse: Boolean) : this(
        DEFAULT_FROM,
        DEFAULT_FROM,
        interpolator,
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    )

    constructor(interpolator: Interpolator?) : this(
        DEFAULT_FROM,
        DEFAULT_FROM,
        interpolator,
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        false
    )

    constructor(duration: Int, alwaysUse: Boolean) : this(
        DEFAULT_FROM,
        DEFAULT_FROM,
        AccelerateDecelerateInterpolator(),
        duration,
        alwaysUse
    )

    constructor(duration: Int) : this(
        DEFAULT_FROM,
        DEFAULT_FROM,
        AccelerateDecelerateInterpolator(),
        duration,
        false
    )

    constructor(alwaysUse: Boolean) : this(
        DEFAULT_FROM,
        DEFAULT_FROM,
        AccelerateDecelerateInterpolator(),
        ImageDisplayer.DEFAULT_ANIMATION_DURATION,
        alwaysUse
    )

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        sketchView.apply {
            clearAnimation()
            setImageDrawable(newDrawable)
            startAnimation(ScaleAnimation(
                this@ZoomInImageDisplayer.fromX,
                1.0f,
                this@ZoomInImageDisplayer.fromY,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                interpolator = this@ZoomInImageDisplayer.interpolator
                duration = this@ZoomInImageDisplayer.duration.toLong()
            })
        }
    }

    override fun toString(): String =
        "%s(duration=%d,fromX=%s,fromY=%s,interpolator=%s,alwaysUse=%s)".format(
            Locale.US,
            "ZoomInImageDisplayer", duration, fromX, fromY,
            interpolator?.javaClass?.simpleName, isAlwaysUse
        )

    companion object {
        private const val DEFAULT_FROM = 0.5f
    }
}