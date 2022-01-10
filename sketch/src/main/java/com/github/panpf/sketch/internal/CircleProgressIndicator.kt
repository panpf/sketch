package com.github.panpf.sketch.internal

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.RectF
import android.view.View

class CircleProgressIndicator(val sizeDp: Float = DEFAULT_SIZE_DP) : AbsProgressIndicator() {

    companion object {
        const val DEFAULT_SIZE_DP = 50f
    }

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#44000000")
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = Color.WHITE
    }
    private val progressOval = RectF()

    override val key: String by lazy {
        "CircleProgressIndicator(size=$sizeDp)"
    }

    override fun drawIndicator(canvas: Canvas, view: View, progress: Float) {
        val availableWidth = view.width - view.paddingLeft - view.paddingRight.toFloat()
        val availableHeight = view.height - view.paddingTop - view.paddingBottom.toFloat()
        val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

        // background
        val radius = realSize / 2f
        val cx = view.left + view.paddingLeft + (availableWidth / 2f)
        val cy = view.top + view.paddingTop + (availableHeight / 2f)
        canvas.drawCircle(cx, cy, radius, bgPaint)

        // stroke
        val strokeWidth = realSize * 0.02f
        strokePaint.strokeWidth = strokeWidth
        canvas.drawCircle(cx, cy, radius, strokePaint)

        // progress
        progressOval.set(
            cx - radius + strokeWidth,
            cy - radius + strokeWidth,
            cx + radius - strokeWidth,
            cy + radius - strokeWidth,
        )
        val sweepAngle = progress.coerceAtLeast(0.01f) * 360f
        canvas.drawArc(progressOval, 267f, sweepAngle, true, progressPaint)
    }
}