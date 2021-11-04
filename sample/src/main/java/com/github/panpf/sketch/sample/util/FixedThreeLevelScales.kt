package com.github.panpf.sketch.sample.util

import android.content.Context
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.zoom.Sizes
import com.github.panpf.sketch.zoom.ZoomScales

/**
 * 固定的三级缩放比例
 */
class FixedThreeLevelScales : ZoomScales {

    private val scales = floatArrayOf(1.0f, 2.0f, 3.0f)
    private var fullZoomScale = 0f  // 能够看到图片全貌的缩放比例
    private var fillZoomScale = 0f  // 能够让图片填满宽或高的缩放比例
    private var originZoomScale = 0f    // 能够让图片按照真实尺寸一比一显示的缩放比例

    override fun reset(
        context: Context,
        sizes: Sizes,
        scaleType: ScaleType?,
        rotateDegrees: Float,
        readMode: Boolean
    ) {
        val drawableWidth =
            if (rotateDegrees % 180 == 0f) sizes.drawableSize.width else sizes.drawableSize.height
        val drawableHeight =
            if (rotateDegrees % 180 == 0f) sizes.drawableSize.height else sizes.drawableSize.width
        val imageWidth =
            if (rotateDegrees % 180 == 0f) sizes.imageSize.width else sizes.imageSize.height
        val imageHeight =
            if (rotateDegrees % 180 == 0f) sizes.imageSize.height else sizes.imageSize.width
        val widthScale = sizes.viewSize.width.toFloat() / drawableWidth
        val heightScale = sizes.viewSize.height.toFloat() / drawableHeight

        // 小的是完整显示比例，大的是充满比例
        fullZoomScale = widthScale.coerceAtMost(heightScale)
        fillZoomScale = widthScale.coerceAtLeast(heightScale)
        originZoomScale =
            (imageWidth.toFloat() / drawableWidth).coerceAtLeast(imageHeight.toFloat() / drawableHeight)
    }

    override fun getMinZoomScale(): Float {
        return 1.0f
    }

    override fun getMaxZoomScale(): Float {
        return 3.0f
    }

    override fun getInitZoomScale(): Float {
        return 1.0f
    }

    override fun getFullZoomScale(): Float {
        return fullZoomScale
    }

    override fun getFillZoomScale(): Float {
        return fillZoomScale
    }

    override fun getOriginZoomScale(): Float {
        return originZoomScale
    }

    override fun getZoomScales(): FloatArray {
        return scales
    }

    override fun clean() {
        originZoomScale = 1.0f
        fillZoomScale = originZoomScale
        fullZoomScale = fillZoomScale
    }
}