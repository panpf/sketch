package com.github.panpf.sketch

import android.content.Context

internal object SketchHolder {

    private var sketch: Sketch? = null

    @JvmStatic
    fun sketch(context: Context): Sketch =
        sketch ?: synchronized(this) {
            sketch ?: newSketch(context).apply {
                sketch = this
            }
        }

    private fun newSketch(context: Context): Sketch {
        val appContext = context.applicationContext
        return if (appContext is SketchFactory) {
            appContext.newSketch()
        } else {
            Sketch.new(appContext)
        }
    }
}

val Context.sketch: Sketch
    get() = SketchHolder.sketch(this)