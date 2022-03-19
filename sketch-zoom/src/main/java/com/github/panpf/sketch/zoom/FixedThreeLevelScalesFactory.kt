package com.github.panpf.sketch.zoom

import android.content.Context
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.util.Size

/**
 * 固定的三级缩放比例
 */
class FixedThreeLevelScalesFactory : ScalesFactory {

    override fun create(
        context: Context,
        viewSize: Size,
        drawableSize: Size,
        rotateDegrees: Int,
        imageSize: Size,
        scaleType: ScaleType,
        readModeDecider: ReadModeDecider?,
    ): Scales {
        val drawableWidth =
            if (rotateDegrees % 180 == 0) drawableSize.width else drawableSize.height
        val drawableHeight =
            if (rotateDegrees % 180 == 0) drawableSize.height else drawableSize.width
        val imageWidth =
            if (rotateDegrees % 180 == 0) imageSize.width else imageSize.height
        val imageHeight =
            if (rotateDegrees % 180 == 0) imageSize.height else imageSize.width
        val widthScale = viewSize.width.toFloat() / drawableWidth
        val heightScale = viewSize.height.toFloat() / drawableHeight

        // 小的是完整显示比例，大的是充满比例
        val fullZoomScaleCache = widthScale.coerceAtMost(heightScale)
        val fillZoomScaleCache = widthScale.coerceAtLeast(heightScale)
        val originZoomScaleCache =
            (imageWidth.toFloat() / drawableWidth).coerceAtLeast(imageHeight.toFloat() / drawableHeight)
        return Scales(
            min = 1.0f,
            max = 3.0f,
            init = 1.0f,
            full = fullZoomScaleCache,
            fill = fillZoomScaleCache,
            origin = originZoomScaleCache,
            doubleClicks = floatArrayOf(1.0f, 2.0f, 3.0f)
        )
    }
}