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
package com.github.panpf.sketch.test.singleton

import android.annotation.SuppressLint
import android.content.Context
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.Sketch.Builder

/**
 * Get Sketch singleton from any Context
 */
val Context.sketch: Sketch
    get() = SketchSingleton.sketch(this)

internal object SketchSingleton {

    @SuppressLint("StaticFieldLeak")
    private var sketch: Sketch? = null

    @JvmStatic
    fun sketch(context: Context): Sketch =
        sketch ?: synchronized(this) {
            sketch ?: synchronized(this) {
                createSketch(context).apply {
                    sketch = this
                }
            }
        }

    private fun createSketch(context: Context): Sketch {
        val appContext = context.applicationContext
        return if (appContext is TestSketchFactory) {
            appContext.createSketch()
        } else {
            Builder(appContext).build()
        }
    }
}