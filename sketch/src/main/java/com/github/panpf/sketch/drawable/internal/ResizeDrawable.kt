package com.github.panpf.sketch.drawable.internal

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapper
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.findLastSketchDrawable
import kotlin.math.max
import kotlin.math.roundToInt

fun Drawable.toResizeDrawable(resize: Resize?): Drawable =
    if (resize != null) {
        if (this is SketchAnimatableDrawable) {
            ResizeAnimatableDrawable(this, resize)
        } else {
            ResizeDrawable(this, resize)
        }
    } else {
        this
    }

@SuppressLint("RestrictedApi")
open class ResizeDrawable(drawable: Drawable, val resize: Resize) :
    DrawableWrapper(drawable) {

    override fun getMinimumWidth(): Int {
        return intrinsicWidth
    }

    override fun getMinimumHeight(): Int {
        return intrinsicHeight
    }

    override fun getIntrinsicWidth(): Int {
        return resize.width
    }

    override fun getIntrinsicHeight(): Int {
        return resize.height
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        val resizeWidth = resize.width
        val resizeHeight = resize.height
        val wrappedDrawable = wrappedDrawable
        val wrappedWidth = wrappedDrawable.intrinsicWidth
        val wrappedHeight = wrappedDrawable.intrinsicHeight
        val wrappedLeft: Int
        val wrappedTop: Int
        val wrappedRight: Int
        val wrappedBottom: Int
        if (wrappedWidth <= 0 || wrappedHeight <= 0) {
            wrappedLeft = 0
            wrappedTop = 0
            wrappedRight = wrappedWidth.takeIf { it > 0 } ?: resizeWidth
            wrappedBottom = wrappedHeight.takeIf { it > 0 } ?: resizeHeight
        } else {
            val widthRatio = resizeWidth.toFloat() / wrappedWidth
            val heightRatio = resizeHeight.toFloat() / wrappedHeight
            val drawableScale = max(widthRatio, heightRatio)
            val newWrappedWidth = (wrappedWidth * drawableScale).roundToInt()
            val newWrappedHeight = (wrappedHeight * drawableScale).roundToInt()
            val imageSize = wrappedDrawable.findLastSketchDrawable()
                ?.imageInfo?.let { info ->
                    Size(info.width, info.height)
                }
            val resizeScale = resize.getScale(
                imageSize?.width ?: wrappedWidth,
                imageSize?.height ?: wrappedHeight
            )
            when (resizeScale) {
                Scale.START_CROP -> {
                    wrappedLeft = 0
                    wrappedTop = 0
                    wrappedRight = newWrappedWidth + wrappedLeft
                    wrappedBottom = newWrappedHeight + wrappedTop
                }
                Scale.CENTER_CROP -> {
                    wrappedLeft = -(newWrappedWidth - resizeWidth) / 2
                    wrappedTop = -(newWrappedHeight - resizeHeight) / 2
                    wrappedRight = newWrappedWidth + wrappedLeft
                    wrappedBottom = newWrappedHeight + wrappedTop
                }
                Scale.END_CROP -> {
                    wrappedLeft = -(newWrappedWidth - resizeWidth)
                    wrappedTop = -(newWrappedHeight - resizeHeight)
                    wrappedRight = newWrappedWidth + wrappedLeft
                    wrappedBottom = newWrappedHeight + wrappedTop
                }
                Scale.FILL -> {
                    wrappedLeft = 0
                    wrappedTop = 0
                    wrappedRight = resizeWidth
                    wrappedBottom = resizeHeight
                }
            }
        }
        wrappedDrawable.setBounds(wrappedLeft, wrappedTop, wrappedRight, wrappedBottom)
    }
}