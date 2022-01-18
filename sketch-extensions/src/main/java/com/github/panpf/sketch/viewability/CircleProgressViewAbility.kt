package com.github.panpf.sketch.viewability

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.RectF
import com.github.panpf.sketch.viewability.internal.AbsProgressViewAbility

// todo 改成 request 状态，未下载时显示 转转转，下载时显示进度，下载成功消失，下载失败显示失败，点击可以重试
class CircleProgressViewAbility constructor(
    sizeDp: Float = DEFAULT_SIZE_DP,
    private val backgroundColor: Int = BACKGROUND_COLOR,
    private val strokeColor: Int = STROKE_COLOR,
    private val progressColor: Int = PROGRESS_COLOR,
) : AbsProgressViewAbility() {

    companion object {
        const val DEFAULT_SIZE_DP = 50f
        const val BACKGROUND_COLOR: Int = 0x44000000
        const val STROKE_COLOR: Int = Color.WHITE
        const val PROGRESS_COLOR: Int = Color.WHITE
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }
    private val strokePaint = Paint().apply {
        isAntiAlias = true
        color = strokeColor
        style = STROKE
    }
    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = progressColor
    }
    private val progressOval = RectF()
    private val realSize = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f)

    override fun drawIndicator(canvas: Canvas, host: Host, progress: Float) {
        val layoutRect = host.layoutRect
        val paddingRect = host.paddingRect
        val availableWidth = layoutRect.width() - paddingRect.left - paddingRect.right.toFloat()
        val availableHeight = layoutRect.height() - paddingRect.top - paddingRect.bottom.toFloat()

        // background
        val radius = realSize / 2f
        val cx = layoutRect.left + paddingRect.left + (availableWidth / 2f)
        val cy = layoutRect.top + paddingRect.top + (availableHeight / 2f)
        canvas.drawCircle(cx, cy, radius, backgroundPaint)

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

fun ViewAbilityContainerOwner.showCircleProgressIndicator(
    showProgressIndicator: Boolean = true,
    sizeDp: Float = CircleProgressViewAbility.DEFAULT_SIZE_DP,
    backgroundColor: Int = CircleProgressViewAbility.BACKGROUND_COLOR,
    strokeColor: Int = CircleProgressViewAbility.STROKE_COLOR,
    progressColor: Int = CircleProgressViewAbility.PROGRESS_COLOR
) {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is CircleProgressViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (showProgressIndicator) {
        val indicator =
            CircleProgressViewAbility(sizeDp, backgroundColor, strokeColor, progressColor)
        viewAbilityContainer.addViewAbility(indicator)
    }
}