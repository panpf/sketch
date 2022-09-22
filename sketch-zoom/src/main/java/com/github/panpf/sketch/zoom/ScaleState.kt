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

import android.graphics.RectF
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.zoom.ScaleState.Initial
import com.github.panpf.sketch.zoom.ScaleState.Initial.Normal
import com.github.panpf.sketch.zoom.internal.format
import kotlin.math.max
import kotlin.math.min

data class ScaleState(

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
     * Initial scale and translate state
     */
    val initial: Initial,

    /**
     * Double-click to scale the desired scale group
     */
    val doubleClickSteps: FloatArray,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScaleState) return false

        if (min != other.min) return false
        if (max != other.max) return false
        if (full != other.full) return false
        if (fill != other.fill) return false
        if (origin != other.origin) return false
        if (initial != other.initial) return false
        if (!doubleClickSteps.contentEquals(other.doubleClickSteps)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + full.hashCode()
        result = 31 * result + fill.hashCode()
        result = 31 * result + origin.hashCode()
        result = 31 * result + initial.hashCode()
        result = 31 * result + doubleClickSteps.contentHashCode()
        return result
    }

    override fun toString(): String {
        val doubleClickStepsString = doubleClickSteps.joinToString(prefix = "[", postfix = "]") {
            it.format(2).toString()
        }
        return "ScaleState(" +
                "min=${min.format(2)}, " +
                "max=${max.format(2)}, " +
                "full=${full.format(2)}, " +
                "fill=${fill.format(2)}, " +
                "origin=${origin.format(2)}, " +
                "initialState=${initial}, " +
                "doubleClickSteps=${doubleClickStepsString}" +
                ")"
    }

    sealed interface Initial {

        data class FitXy(
            val srcRectF: RectF,
            val dstRectF: RectF
        ) : Initial

        data class Normal(
            val scale: Float,
            val translateX: Float,
            val translateY: Float
        ) : Initial {
            override fun toString(): String {
                return "Normal(" +
                        "scale=${scale.format(2)}, " +
                        "translateX=${translateX.format(2)}, " +
                        "translateY=${translateY.format(2)}" +
                        ")"
            }
        }
    }

    interface Factory {

        fun create(
            viewSize: Size,
            imageSize: Size,
            drawableSize: Size,
            rotateDegrees: Int,
            scaleType: ScaleType,
            readModeDecider: ReadModeDecider?,
        ): ScaleState
    }

    companion object {
        @JvmStatic
        val EMPTY = ScaleState(
            min = 1.0f,
            max = 1.0f,
            full = 1.0f,
            fill = 1.0f,
            origin = 1.0f,
            initial = Normal(1.0f, 0f, 0f),
            doubleClickSteps = floatArrayOf(1.0f, 1.0f),
        )
    }
}

open class DefaultScaleStateFactory : ScaleState.Factory {

    override fun create(
        viewSize: Size,
        imageSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): ScaleState {
        if (drawableSize.isEmpty || viewSize.isEmpty) {
            return ScaleState.EMPTY
        }

        val drawableWidth =
            if (rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val imageWidth = if (!imageSize.isEmpty) {
            if (rotateDegrees % 180 == 0) imageSize.width else imageSize.height
        } else {
            drawableWidth
        }
        val imageHeight = if (!imageSize.isEmpty) {
            if (rotateDegrees % 180 == 0) imageSize.height else imageSize.width
        } else {
            drawableHeight
        }
        val viewWidth = viewSize.width
        val viewHeight = viewSize.height
        val widthScale = viewWidth.toFloat() / drawableWidth
        val heightScale = viewHeight.toFloat() / drawableHeight
        val drawableThanViewLarge =
            drawableWidth > viewWidth || drawableHeight > viewHeight
        val imageAspectRatio = imageWidth.toFloat().div(imageHeight).format(2)
        val targetAspectRatio = viewWidth.toFloat().div(viewHeight).format(2)
        val sameDirection = imageAspectRatio == 1.0f
                || targetAspectRatio == 1.0f
                || (imageAspectRatio > 1.0f && targetAspectRatio > 1.0f)
                || (imageAspectRatio < 1.0f && targetAspectRatio < 1.0f)

        // The width or height of the drawable fills the view
        val fullScale = min(widthScale, heightScale)
        // The width and height of drawable fill the view at the same time
        val fillScale = max(widthScale, heightScale)
        // Enlarge drawable to the same size as its original image
        val originScale =
            max(imageWidth.toFloat() / drawableWidth, imageHeight.toFloat() / drawableHeight)
        // The drawable size remains the same
        val keepScale = 1.0f

        val minScale: Float
        val stepsBigScale: Float
        val initial: Initial
        @Suppress("UnnecessaryVariable")
        @Suppress("KotlinConstantConditions")
        when {
            readModeDecider?.should(imageWidth, imageHeight, viewWidth, viewHeight) == true -> {
                val initScale = fillScale
                minScale = fullScale
                initial = Normal(initScale, 0f, 0f)
                stepsBigScale = max(originScale, initScale)
            }
            scaleType == ScaleType.CENTER
                    || (scaleType == ScaleType.CENTER_INSIDE && !drawableThanViewLarge) -> {
                val initScale = keepScale
                minScale = keepScale
                initial = Normal(
                    scale = initScale,
                    translateX = (viewWidth - drawableWidth) / 2f,
                    translateY = (viewHeight - drawableHeight) / 2f,
                )
                stepsBigScale =
                    floatArrayOf(originScale, fullScale, initScale * 2f).maxOrNull()!!
            }
            scaleType == ScaleType.CENTER_CROP -> {
                val initScale = fillScale
                minScale = fillScale
                initial = Normal(
                    scale = initScale,
                    translateX = (viewWidth - drawableWidth * initScale) / 2f,
                    translateY = (viewHeight - drawableHeight * initScale) / 2f,
                )
                stepsBigScale = max(originScale, initScale * 2f)
            }
            scaleType == ScaleType.FIT_START -> {
                val initScale = fullScale
                minScale = fullScale
                initial = Normal(initScale, 0f, 0f)
                stepsBigScale = if (sameDirection) {
                    max(originScale, initScale * 2f)
                } else {
                    floatArrayOf(originScale, initScale * 2f, fillScale).maxOrNull()!!
                }
            }
            scaleType == ScaleType.FIT_CENTER
                    || (scaleType == ScaleType.CENTER_INSIDE && drawableThanViewLarge) -> {
                val initScale = fullScale
                minScale = fullScale
                initial = Normal(
                    scale = initScale,
                    translateX = 0f,
                    translateY = (viewHeight - drawableHeight * initScale) / 2f
                )
                stepsBigScale = if (sameDirection) {
                    max(originScale, initScale * 2f)
                } else {
                    floatArrayOf(originScale, initScale * 2f, fillScale).maxOrNull()!!
                }
            }
            scaleType == ScaleType.FIT_END -> {
                val initScale = fullScale
                minScale = fullScale
                initial = Normal(
                    scale = initScale,
                    translateX = 0f,
                    translateY = viewHeight - drawableHeight * initScale
                )
                stepsBigScale = if (sameDirection) {
                    max(originScale, initScale * 2f)
                } else {
                    floatArrayOf(originScale, initScale * 2f, fillScale).maxOrNull()!!
                }
            }
            else -> {   // FIX_XY
                val initScale = keepScale
                minScale = keepScale
                initial = Initial.FitXy(
                    srcRectF = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat()),
                    dstRectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                )
                stepsBigScale = max(originScale, initScale * 2f)
            }
        }
        val steps = floatArrayOf(minScale, stepsBigScale)
        val maxScale = stepsBigScale * 2f
        return ScaleState(
            min = minScale,
            max = maxScale,
            full = fullScale,
            fill = fillScale,
            origin = originScale,
            initial = initial,
            doubleClickSteps = steps,
        )
    }
}