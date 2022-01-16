package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.viewability.showDataFrom

class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {

    init {
        context.appSettingsService.apply {
            showDataFrom.observeFromView(this@MyImageView) {
                showDataFrom(it == true)
            }
        }
    }
}