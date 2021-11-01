package com.github.panpf.sketch.display

import android.graphics.drawable.Drawable
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import com.github.panpf.sketch.SketchView
import java.util.*

/**
 * 渐入动画
 */
class FadeInImageDisplayer @JvmOverloads constructor(
    override val duration: Int = ImageDisplayer.DEFAULT_ANIMATION_DURATION,
    override val isAlwaysUse: Boolean = false
) : ImageDisplayer {

    constructor(alwaysUse: Boolean) : this(ImageDisplayer.DEFAULT_ANIMATION_DURATION, alwaysUse)

    override fun display(sketchView: SketchView, newDrawable: Drawable) {
        sketchView.apply {
            clearAnimation()
            setImageDrawable(newDrawable)
            startAnimation(AlphaAnimation(0.0f, 1.0f).apply {
                interpolator = DecelerateInterpolator()
                duration = this@FadeInImageDisplayer.duration.toLong()
            })
        }
    }

    override fun toString(): String = "%s(duration=%d,alwaysUse=%s)"
        .format(Locale.US, "FadeInImageDisplayer", duration, isAlwaysUse)
}