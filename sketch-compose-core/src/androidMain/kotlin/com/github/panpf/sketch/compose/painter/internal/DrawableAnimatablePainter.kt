package com.github.panpf.sketch.compose.painter.internal

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.unit.LayoutDirection
import com.github.panpf.sketch.compose.painter.AnimatablePainter
import com.github.panpf.sketch.drawable.internal.toLogString
import kotlin.math.roundToInt

private val MAIN_HANDLER by lazy(LazyThreadSafetyMode.NONE) {
    Handler(Looper.getMainLooper())
}

class DrawableAnimatablePainter(
    val drawable: Drawable
) : Painter(), RememberObserver, AnimatablePainter, SketchPainter {
    private var drawInvalidateTick by mutableIntStateOf(0)
    private var drawableIntrinsicSize by mutableStateOf(drawable.intrinsicSize)

    private val animatable: Animatable

    init {
        require(drawable is Animatable) {
            "drawable must be Animatable"
        }
        animatable = drawable
    }

    private val callback: Drawable.Callback by lazy {
        object : Drawable.Callback {
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

    init {
        if (drawable.intrinsicWidth >= 0 && drawable.intrinsicHeight >= 0) {
            // Update the drawable's bounds to match the intrinsic size
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        }
    }

    override fun onRemembered() {
        drawable.callback = callback
        drawable.setVisible(true, true)
        if (drawable is Animatable) drawable.start()
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        if (drawable is Animatable) drawable.stop()
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
        if (Build.VERSION.SDK_INT >= 23) {
            return drawable.setLayoutDirection(
                when (layoutDirection) {
                    LayoutDirection.Ltr -> View.LAYOUT_DIRECTION_LTR
                    LayoutDirection.Rtl -> View.LAYOUT_DIRECTION_RTL
                }
            )
        }
        return false
    }

    override val intrinsicSize: Size get() = drawableIntrinsicSize

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

    override fun start() {
        animatable.start()
    }

    override fun stop() {
        animatable.stop()
    }

    override fun isRunning(): Boolean {
        return animatable.isRunning
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DrawableAnimatablePainter
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawableAnimatablePainter(drawable=${drawable.toLogString()})"
    }
}

private val Drawable.intrinsicSize: Size
    get() = when {
        // Only return a finite size if the drawable has an intrinsic size
        intrinsicWidth >= 0 && intrinsicHeight >= 0 -> {
            Size(width = intrinsicWidth.toFloat(), height = intrinsicHeight.toFloat())
        }

        else -> Size.Unspecified
    }
