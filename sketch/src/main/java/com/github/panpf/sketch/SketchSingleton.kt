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
package com.github.panpf.sketch

import android.content.Context
import com.github.panpf.sketch.Sketch.Builder

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
        return if (appContext is SketchFactory) {
            appContext.createSketch()
        } else {
            Builder(appContext).build()
        }
    }
}