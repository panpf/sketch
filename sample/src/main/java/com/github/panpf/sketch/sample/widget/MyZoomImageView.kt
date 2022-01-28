package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.zoom.AbsZoomImageView

class MyZoomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsZoomImageView(context, attrs) {
    override val sketch: Sketch
        get() = context.sketch
}