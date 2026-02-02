package com.github.panpf.sketch.singleton.android.test

import android.app.Application
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch

class TestApplication : Application(), SingletonSketch.Factory {

    override fun createSketch(context: PlatformContext): Sketch {
        return Sketch(context)
    }
}