/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Get Sketch singleton from any Context
 */
val Context.sketch: Sketch
    get() = SketchSingleton.sketch(this)

object SketchSingleton {

    @SuppressLint("StaticFieldLeak")
    private var sketch: Sketch? = null
    private var sketchFactory: SketchFactory? = null

    /**
     * Get the singleton [Sketch].
     */
    @JvmStatic
    fun sketch(context: Context): Sketch =
        sketch ?: synchronized(this) {
            sketch ?: synchronized(this) {
                createSketch(context).apply {
                    sketch = this
                }
            }
        }

    /**
     * Set the singleton [Sketch].
     * Prefer using `setSketch(SketchFactory)` to create the [Sketch] lazily.
     */
    @JvmStatic
    @Synchronized
    fun setSketch(sketch: Sketch) {
        this.sketch?.shutdown()
        this.sketch = sketch
        this.sketchFactory = null
    }

    /**
     * Set the [SketchFactory] that will be used to create the singleton [Sketch].
     * The [factory] is guaranteed to be called at most once.
     *
     * NOTE: [factory] will take precedence over an [Application] that implements [SketchFactory].
     */
    @JvmStatic
    @Synchronized
    fun setSketch(factory: SketchFactory) {
        this.sketch?.shutdown()
        this.sketch = null
        this.sketchFactory = factory
    }

    /**
     * Clear the [Sketch] and [SketchFactory] held by this class.
     *
     * This method is useful for testing and its use is discouraged in production code.
     */
    @JvmStatic
    @Synchronized
    fun reset() {
        this.sketch?.shutdown()
        this.sketch = null
        this.sketchFactory = null
    }

    private fun createSketch(context: Context): Sketch {
        val appContext = context.applicationContext
        return sketchFactory?.createSketch()
            ?: (appContext as? SketchFactory)?.createSketch()
            ?: Sketch.Builder(appContext).build()
    }
}