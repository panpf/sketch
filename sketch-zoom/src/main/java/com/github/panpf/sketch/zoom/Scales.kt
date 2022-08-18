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
package com.github.panpf.sketch.zoom

import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.internal.format

data class Scales(
    /**
     * Maximum initial scaling ratio
     */
    val init: Float,

    /**
     * Minimum scale
     */
    val min: Float,

    /**
     * Maximum scale
     */
    val max: Float,

    /**
     * You can see the full scale of the picture
     */
    val full: Float,

    /**
     * Make the width or height fill the screen's zoom ratio
     */
    val fill: Float,

    /**
     * The ability to display images in one-to-one scale to their true size
     */
    val origin: Float,

    /**
     * Double-click to scale the desired scale group
     */
    val steps: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Scales) return false

        if (init != other.init) return false
        if (min != other.min) return false
        if (max != other.max) return false
        if (full != other.full) return false
        if (fill != other.fill) return false
        if (origin != other.origin) return false
        if (!steps.contentEquals(other.steps)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = init.hashCode()
        result = 31 * result + min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + full.hashCode()
        result = 31 * result + fill.hashCode()
        result = 31 * result + origin.hashCode()
        result = 31 * result + steps.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Scales(" +
                "init=${init.format(2)}, " +
                "min=${min.format(2)}, " +
                "max=${max.format(2)}, " +
                "full=${full.format(2)}, " +
                "fill=${fill.format(2)}, " +
                "origin=${origin.format(2)}, " +
                "steps=${
                    steps.joinToString(prefix = "[", postfix = "]") {
                        it.format(2).toString()
                    }
                }})"
    }

    companion object {
        @JvmStatic
        val EMPTY = Scales(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, floatArrayOf(1.0f, 1.0f))
    }
}

interface ScalesFactory {
    fun create(
        sketch: Sketch,
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales
}