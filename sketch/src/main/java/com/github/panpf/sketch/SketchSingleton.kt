package com.github.panpf.sketch

import android.content.Context
import com.github.panpf.sketch.Sketch.Builder

// todo 内部减少对此函数的使用
val Context.sketch: Sketch
    get() = SketchSingleton.sketch(this)

internal object SketchSingleton {

    private var sketch: Sketch? = null

    @JvmStatic
    fun sketch(context: Context): Sketch =
        sketch ?: synchronized(this) {
            sketch ?: synchronized(this) {
                newSketch(context).apply {
                    sketch = this
                }
            }
        }

    private fun newSketch(context: Context): Sketch {
        val appContext = context.applicationContext
        val builder = Builder(appContext)
        if (appContext is SketchConfigurator) {
            appContext.createSketchConfig().invoke(builder)
        }
        return builder.build()
    }
}