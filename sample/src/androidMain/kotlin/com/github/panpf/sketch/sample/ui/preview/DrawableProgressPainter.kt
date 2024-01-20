package com.github.panpf.sketch.sample.ui.preview

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.unit.LayoutDirection
import com.github.panpf.sketch.drawable.ProgressDrawable
import kotlin.math.roundToInt

abstract class AbsDrawableProgressPainter : Painter() {
    abstract var progress: Float
    var drawInvalidateTick by mutableIntStateOf(0)
}

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
fun rememberDrawableProgressPainter(drawable: ProgressDrawable?): AbsDrawableProgressPainter =
    remember(drawable) {
        when (drawable) {
            null -> EmptyDrawableProgressPainter
//        is BitmapDrawable -> BitmapPainter(drawable.bitmap.asImageBitmap())
//        is ColorDrawable -> ColorPainter(Color(drawable.color))
            // Since the DrawableProgressPainter will be remembered and it implements RememberObserver, it
            // will receive the necessary events
            else -> DrawableProgressPainter(drawable.mutate())
        }
    }

/**
 * A [Painter] which draws an Android [Drawable] and supports [Animatable] drawables. Instances
 * should be remembered to be able to start and stop [Animatable] animations.
 *
 * Instances are usually retrieved from [rememberDrawableProgressPainter].
 */
class DrawableProgressPainter internal constructor(
    val drawable: ProgressDrawable
) : AbsDrawableProgressPainter(), RememberObserver {

    private var drawableIntrinsicSize by mutableStateOf(drawable.intrinsicSize)

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

    override var progress: Float
        get() = drawable.progress
        set(value) {
            drawable.progress = value
        }

    override val intrinsicSize: Size get() = drawableIntrinsicSize

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

    override fun DrawScope.onDraw() {
        if (!drawable.isVisible) return
        drawIntoCanvas { canvas ->
            // Update the Drawable's bounds
            drawable.setBounds(0, 0, size.width.roundToInt(), size.height.roundToInt())

            canvas.withSave {
                drawable.draw(canvas.nativeCanvas)
            }
        }
    }
}

private val MAIN_HANDLER by lazy(LazyThreadSafetyMode.NONE) {
    Handler(Looper.getMainLooper())
}

private val Drawable.intrinsicSize: Size
    get() = when {
        // Only return a finite size if the drawable has an intrinsic size
        intrinsicWidth >= 0 && intrinsicHeight >= 0 -> {
            Size(width = intrinsicWidth.toFloat(), height = intrinsicHeight.toFloat())
        }

        else -> Size.Unspecified
    }

internal object EmptyDrawableProgressPainter : AbsDrawableProgressPainter() {
    override var progress: Float = 0f
    override val intrinsicSize: Size get() = Size.Unspecified
    override fun DrawScope.onDraw() {}
}