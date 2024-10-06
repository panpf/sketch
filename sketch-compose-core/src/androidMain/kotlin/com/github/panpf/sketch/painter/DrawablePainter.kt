/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.painter

import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.Build.VERSION
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import com.github.panpf.sketch.drawable.EquitableDrawable
import com.github.panpf.sketch.util.toLogString
import kotlin.math.roundToInt

/**
 * Remembers [Drawable] wrapped up as a [Painter]. This function attempts to un-wrap the
 * drawable contents and use Compose primitives where possible.
 *
 * If the provided [drawable] is `null`, an empty no-op painter is returned.
 *
 * This function tries to dispatch lifecycle events to [drawable] as much as possible from
 * within Compose.
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest.testRememberDrawablePainter
 */
@Composable
fun rememberDrawablePainter(drawable: EquitableDrawable): Painter =
    remember(drawable) { drawable.asPainter() }

/**
 * Converts a [Drawable] to a [Painter]. This function attempts to un-wrap the drawable contents
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest.testAsPainter
 */
fun Drawable?.asPainter(): Painter {
    return when (this) {
        null -> EmptyPainter
        is Animatable -> DrawableAnimatablePainter(this.mutate())
        is BitmapDrawable -> BitmapPainter(this.bitmap.asImageBitmap())
        is ColorDrawable -> ColorPainter(Color(this.color))
        // Since the DrawablePainter will be remembered and it implements RememberObserver, it
        //  will receive the necessary events
        else -> DrawablePainter(this.mutate())
    }
}

/**
 * A [Painter] which draws an Android [Drawable] and supports [Animatable] drawables. Instances
 * should be remembered to be able to start and stop [Animatable] animations.
 *
 * Instances are usually retrieved from [rememberDrawablePainter].
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest
 */
@Stable
open class DrawablePainter(
    val drawable: Drawable
) : Painter(), RememberObserver, SketchPainter {
    private var drawInvalidateTick by mutableIntStateOf(0)
    private var drawableIntrinsicSize by mutableStateOf(drawable.intrinsicSize)

    private val callback: Callback by lazy {
        object : Callback {
            override fun invalidateDrawable(d: Drawable) {
                // Update the tick so that we get re-drawn
                drawInvalidateTick++
                // Update our intrinsic size too
                drawableIntrinsicSize = drawable.intrinsicSize
            }

            override fun scheduleDrawable(d: Drawable, what: Runnable, time: Long) {
                MAIN_HANDLER.postAtTime(what, time)
            }

            override fun unscheduleDrawable(d: Drawable, what: Runnable) {
                MAIN_HANDLER.removeCallbacks(what)
            }
        }
    }

    /*
     * Why do you need to remember to count?
     *
     * Because when RememberObserver is passed as a parameter of the Composable function, the onRemembered method
     * will be called when the Composable function is executed for the first time, causing it to be remembered multiple times.
     */
    internal var rememberedCount = 0

    override val intrinsicSize: Size get() = drawableIntrinsicSize

    init {
        if (drawable.intrinsicWidth >= 0 && drawable.intrinsicHeight >= 0) {
            // Update the drawable's bounds to match the intrinsic size
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        }
    }

    override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return
        onFirstRemembered()
    }

    protected open fun onFirstRemembered() {
        drawable.callback = callback
        drawable.setVisible(true, true)
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return
        onLastRemembered()
    }

    protected open fun onLastRemembered() {
        drawable.setVisible(false, false)
        drawable.callback = null
    }

    override fun applyAlpha(alpha: Float): Boolean {
        drawable.alpha = (alpha * 255).roundToInt().coerceIn(0, 255)
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        drawable.colorFilter = colorFilter?.asAndroidColorFilter()
        return true
    }

    override fun applyLayoutDirection(layoutDirection: LayoutDirection): Boolean {
        if (VERSION.SDK_INT >= 23) {
            return drawable.setLayoutDirection(
                when (layoutDirection) {
                    Ltr -> View.LAYOUT_DIRECTION_LTR
                    Rtl -> View.LAYOUT_DIRECTION_RTL
                }
            )
        }
        return false
    }

    override fun DrawScope.onDraw() {
        drawIntoCanvas { canvas ->
            // Reading this ensures that we invalidate when invalidateDrawable() is called
            drawInvalidateTick

            // Update the Drawable's bounds
            drawable.setBounds(0, 0, size.width.roundToInt(), size.height.roundToInt())

            canvas.withSave {
                drawable.draw(canvas.nativeCanvas)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DrawablePainter
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawablePainter(drawable=${drawable.toLogString()})"
    }
}

/**
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest.testMainHandler
 */
internal val MAIN_HANDLER by lazy(LazyThreadSafetyMode.NONE) {
    Handler(Looper.getMainLooper())
}

/**
 * Get the intrinsic size of Drawable, if Drawable has no intrinsic size, return [Size.Unspecified]
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest.testDrawableIntrinsicSize
 */
internal val Drawable.intrinsicSize: Size
    get() = when {
        // Only return a finite size if the drawable has an intrinsic size
        intrinsicWidth >= 0 && intrinsicHeight >= 0 -> {
            Size(width = intrinsicWidth.toFloat(), height = intrinsicHeight.toFloat())
        }

        else -> Size.Unspecified
    }

/**
 * Empty Painter
 *
 * @see com.github.panpf.sketch.compose.core.android.test.painter.DrawablePainterTest.testEmptyPainter
 */
internal object EmptyPainter : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() {}
}