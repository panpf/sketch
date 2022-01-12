package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.Observer
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.internal.showDataFrom
import com.github.panpf.sketch.sample.appSettingsService

class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SketchImageView(context, attrs) {

    private val dataFromObserver = Observer<Boolean> {
        showDataFrom(it == true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context.appSettingsService.showDataFrom.observeForever(dataFromObserver)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.appSettingsService.showDataFrom.removeObserver(dataFromObserver)
    }
}