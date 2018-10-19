package me.panpf.sketch.sample.util;

import android.content.Context;
import android.widget.ImageView;

import me.panpf.sketch.zoom.Sizes;
import me.panpf.sketch.zoom.ZoomScales;

/**
 * 固定的三级缩放比例
 */
public class FixedThreeLevelScales implements ZoomScales {

    private float[] scales = new float[]{1.0f, 2.0f, 3.0f};
    private float fullZoomScale; // 能够看到图片全貌的缩放比例
    private float fillZoomScale;    // 能够让图片填满宽或高的缩放比例
    private float originZoomScale;  // 能够让图片按照真实尺寸一比一显示的缩放比例

    @Override
    public void reset(final Context context, final Sizes sizes, final ImageView.ScaleType scaleType, final float rotateDegrees, final boolean readMode) {
        final int drawableWidth = rotateDegrees % 180 == 0 ? sizes.drawableSize.getWidth() : sizes.drawableSize.getHeight();
        final int drawableHeight = rotateDegrees % 180 == 0 ? sizes.drawableSize.getHeight() : sizes.drawableSize.getWidth();
        final int imageWidth = rotateDegrees % 180 == 0 ? sizes.imageSize.getWidth() : sizes.imageSize.getHeight();
        final int imageHeight = rotateDegrees % 180 == 0 ? sizes.imageSize.getHeight() : sizes.imageSize.getWidth();

        final float widthScale = (float) sizes.viewSize.getWidth() / drawableWidth;
        final float heightScale = (float) sizes.viewSize.getHeight() / drawableHeight;

        // 小的是完整显示比例，大的是充满比例
        fullZoomScale = Math.min(widthScale, heightScale);
        fillZoomScale = Math.max(widthScale, heightScale);
        originZoomScale = Math.max((float) imageWidth / drawableWidth, (float) imageHeight / drawableHeight);
    }

    @Override
    public float getMinZoomScale() {
        return 1.0f;
    }

    @Override
    public float getMaxZoomScale() {
        return 3.0f;
    }

    @Override
    public float getInitZoomScale() {
        return 1.0f;
    }

    @Override
    public float getFullZoomScale() {
        return fullZoomScale;
    }

    @Override
    public float getFillZoomScale() {
        return fillZoomScale;
    }

    @Override
    public float getOriginZoomScale() {
        return originZoomScale;
    }

    @Override
    public float[] getZoomScales() {
        return scales;
    }

    @Override
    public void clean() {
        fullZoomScale = fillZoomScale = originZoomScale = 1.0f;
    }
}
