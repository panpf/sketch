package com.github.panpf.sketch.compose.painter

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
import com.github.panpf.sketch.drawable.internal.toLogString
import kotlin.math.roundToInt

/**
 * Remembers [Drawable] wrapped up as a [Painter]. This function attempts to un-wrap the
 * drawable contents and use Compose primitives where possible.
 *
 * If the provided [drawable] is `null`, an empty no-op painter is returned.
 *
 * This function tries to dispatch lifecycle events to [drawable] as much as possible from
 * within Compose.
 */
@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter = remember(drawable) {
    drawable.asPainter()
}

fun Drawable?.asPainter(): Painter {
    return when (this) {
        null -> EmptyPainter
        is Animatable -> DrawableAnimatablePainter(this.mutate())
        is BitmapDrawable -> BitmapPainter(this.bitmap.asImageBitmap())
        is ColorDrawable -> ColorPainter(Color(this.color))
        // Since the DrawablePainter will be remembered and it implements RememberObserver, it
        // will receive the necessary events
        else -> DrawablePainter(this.mutate())
    }
}

/**
 * A [Painter] which draws an Android [Drawable] and supports [Animatable] drawables. Instances
 * should be remembered to be able to start and stop [Animatable] animations.
 *
 * Instances are usually retrieved from [rememberDrawablePainter].
 */
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawablePainter) return false
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawablePainter(drawable=${drawable.toLogString()})"
    }
}

class DrawableAnimatablePainter(
    drawable: Drawable
) : DrawablePainter(drawable), com.github.panpf.sketch.compose.painter.AnimatablePainter {

    private val animatable: Animatable

    init {
        require(drawable is Animatable) {
            "drawable must be Animatable"
        }
        animatable = drawable
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
        if (other !is DrawableAnimatablePainter) return false
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawableAnimatablePainter(drawable=${drawable.toLogString()})"
    }
}

internal val MAIN_HANDLER by lazy(LazyThreadSafetyMode.NONE) {
    Handler(Looper.getMainLooper())
}

internal val Drawable.intrinsicSize: Size
    get() = when {
        // Only return a finite size if the drawable has an intrinsic size
        intrinsicWidth >= 0 && intrinsicHeight >= 0 -> {
            Size(width = intrinsicWidth.toFloat(), height = intrinsicHeight.toFloat())
        }

        else -> Size.Unspecified
    }

internal object EmptyPainter : Painter() {
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() {}
}