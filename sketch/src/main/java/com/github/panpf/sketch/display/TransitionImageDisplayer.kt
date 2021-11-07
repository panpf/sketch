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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.drawable.SketchTransitionDrawable
import com.github.panpf.sketch.util.SketchUtils
import java.util.*

/**
 * 过渡效果的图片显示器
 */
class TransitionImageDisplayer @JvmOverloads constructor(
    /**
     * 获取持续时间，单位毫秒
     */
    override val duration: Int = ImageDisplayer.DEFAULT_ANIMATION_DURATION,
    override val isAlwaysUse: Boolean = false
) : ImageDisplayer {
    private var disableCrossFade = false

    constructor(alwaysUse: Boolean) : this(ImageDisplayer.DEFAULT_ANIMATION_DURATION, alwaysUse)

    fun setDisableCrossFade(disableCrossFade: Boolean): TransitionImageDisplayer {
        this.disableCrossFade = disableCrossFade
        return this
    }

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        if (newDrawable is SketchGifDrawable) {
            sketchView.apply {
                clearAnimation()
                setImageDrawable(newDrawable)
            }
        } else {
            val oldDrawable = SketchUtils.getLastDrawable(sketchView.getDrawable())
                ?: ColorDrawable(Color.TRANSPARENT)
            if (oldDrawable is SketchDrawable
                && oldDrawable !is SketchLoadingDrawable
                && newDrawable is SketchDrawable
                && oldDrawable.key == newDrawable.key
            ) {
                sketchView.setImageDrawable(newDrawable)
            } else {
                val transitionDrawable: TransitionDrawable =
                    SketchTransitionDrawable(oldDrawable, newDrawable)
                sketchView.clearAnimation()
                sketchView.setImageDrawable(transitionDrawable)
                transitionDrawable.isCrossFadeEnabled = !disableCrossFade
                transitionDrawable.startTransition(duration)
            }
        }
    }

    override fun toString(): String = "%s(duration=%d,alwaysUse=%s)"
        .format(Locale.US, "TransitionImageDisplayer", duration, isAlwaysUse)
}