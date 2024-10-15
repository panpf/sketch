package com.github.panpf.sketch.sample.ui.components

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.DST_OUT
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.core.graphics.toRectF
import androidx.core.graphics.withRotation
import com.github.panpf.sketch.util.Size

class NewMoonLoadingDrawable(val intrinsicSize: Size) : Drawable(), Animatable {

    private val dstPaint = Paint().apply {
        color = Color.BLACK
    }
    private val srcPaint = Paint().apply {
        xfermode = PorterDuffXfermode(DST_OUT)
        color = Color.GRAY
    }
    private var rotation = 0f
    private var running = false

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        canvas.withRotation(
            degrees = rotation,
            pivotX = bounds.centerX().toFloat(),
            pivotY = bounds.centerY().toFloat()
        ) {
            saveLayer(bounds.toRectF(), dstPaint)
            drawCircle(
                /* cx = */ bounds.centerX().toFloat(),
                /* cy = */ bounds.centerY().toFloat(),
                /* radius = */ bounds.width() / 2f,
                /* paint = */ dstPaint,
            )
            drawCircle(
                /* cx = */ bounds.centerX().toFloat(),
                /* cy = */ bounds.centerY().toFloat() - (bounds.height() * 0.05f),
                /* radius = */ bounds.width() / 2f,
                /* paint = */ srcPaint,
            )
            restore()
        }

        if (running) {
            rotation = (rotation + 5) % 360
            invalidateSelf()
        }
    }

    override fun setTintList(tint: ColorStateList?) {
        super.setTintList(tint)
        dstPaint.color = tint?.defaultColor ?: Color.BLACK
        invalidateSelf()
    }

    override fun getIntrinsicWidth(): Int {
        return intrinsicSize.width
    }

    override fun getIntrinsicHeight(): Int {
        return intrinsicSize.height
    }

    override fun setAlpha(alpha: Int) {
        dstPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        dstPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun start() {
        if (running) return
        running = true
        invalidateSelf()
    }

    override fun stop() {
        if (!running) return
        running = false
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return running
    }
}