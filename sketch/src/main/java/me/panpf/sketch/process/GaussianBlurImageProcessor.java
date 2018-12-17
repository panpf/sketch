package me.panpf.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.request.Resize;

/**
 * 高斯模糊图片处理器
 */
public class GaussianBlurImageProcessor extends WrappedImageProcessor {

    private static final int NO_LAYER_COLOR = -1;
    private static final int DEFAULT_RADIUS = 15;

    private int radius; // 模糊半径，取值为0到100
    private int maskColor; // 图层颜色，在模糊后的图片上加一层颜色

    private GaussianBlurImageProcessor(int radius, int maskColor, WrappedImageProcessor wrappedImageProcessor) {
        super(wrappedImageProcessor);
        this.radius = radius;
        this.maskColor = maskColor;
    }

    /**
     * 创建一个指定半径和图层颜色的高斯模糊图片处理器
     *
     * @param radius                模糊半径，取值为0到100
     * @param layerColor            图层颜色，在模糊后的图片上加一层颜色
     * @param wrappedImageProcessor 嵌套一个图片处理器
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor make(int radius, int layerColor, WrappedImageProcessor wrappedImageProcessor) {
        return new GaussianBlurImageProcessor(radius, layerColor, wrappedImageProcessor);
    }

    /**
     * 创建一个指定半径和图层颜色的高斯模糊图片处理器
     *
     * @param radius     模糊半径，取值为0到100
     * @param layerColor 图层颜色，在模糊后的图片上加一层颜色
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor make(int radius, int layerColor) {
        return new GaussianBlurImageProcessor(radius, layerColor, null);
    }

    /**
     * 创建一个图层颜色的高斯模糊图片处理器
     *
     * @param layerColor            图层颜色，在模糊后的图片上加一层颜色
     * @param wrappedImageProcessor 嵌套一个图片处理器
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor makeLayerColor(int layerColor, WrappedImageProcessor wrappedImageProcessor) {
        return new GaussianBlurImageProcessor(DEFAULT_RADIUS, layerColor, wrappedImageProcessor);
    }

    /**
     * 创建一个图层颜色的高斯模糊图片处理器
     *
     * @param layerColor 图层颜色，在模糊后的图片上加一层颜色
     * @return GaussianBlurImageProcessor
     */
    public static GaussianBlurImageProcessor makeLayerColor(int layerColor) {
        return new GaussianBlurImageProcessor(DEFAULT_RADIUS, layerColor, null);
    }

    /**
     * 创建一个指定半径的高斯模糊图片处理器
     *
     * @param radius                模糊半径，取值为 0 到 100
     * @param wrappedImageProcessor 嵌套一个图片处理器
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor makeRadius(int radius, WrappedImageProcessor wrappedImageProcessor) {
        return new GaussianBlurImageProcessor(radius, NO_LAYER_COLOR, wrappedImageProcessor);
    }

    /**
     * 创建一个指定半径的高斯模糊图片处理器
     *
     * @param radius 模糊半径，取值为 0 到 100
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor makeRadius(int radius) {
        return new GaussianBlurImageProcessor(radius, NO_LAYER_COLOR, null);
    }

    /**
     * 创建一个半径为15的高斯模糊图片处理器
     *
     * @param wrappedImageProcessor 嵌套一个图片处理器
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor make(WrappedImageProcessor wrappedImageProcessor) {
        return new GaussianBlurImageProcessor(DEFAULT_RADIUS, NO_LAYER_COLOR, wrappedImageProcessor);
    }

    /**
     * 创建一个半径为 15 的高斯模糊图片处理器
     *
     * @return GaussianBlurImageProcessor
     */
    @SuppressWarnings("unused")
    public static GaussianBlurImageProcessor make() {
        return new GaussianBlurImageProcessor(DEFAULT_RADIUS, NO_LAYER_COLOR, null);
    }

    /**
     * 快速高斯模糊
     */
    public static Bitmap fastGaussianBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig() != null ? sentBitmap.getConfig() : Bitmap.Config.ARGB_8888, true);
        }

        try {
            if (radius < 1) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }

            bitmap.setPixels(pix, 0, w, 0, 0, w, h);

            return (bitmap);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (bitmap != null && bitmap != sentBitmap) {
                bitmap.recycle();
            }
            return null;
        }
    }

    @NonNull
    @Override
    public Bitmap onProcess(@NonNull Sketch sketch, @NonNull Bitmap bitmap, @Nullable Resize resize, boolean lowQualityImage) {
        if (bitmap.isRecycled()) {
            return bitmap;
        }

        // blur handle
        Bitmap blurBitmap = fastGaussianBlur(bitmap, radius, bitmap.getConfig() != null && bitmap.isMutable());

        if (blurBitmap == null) {
            return bitmap;
        }

        // layer color handle
        if (maskColor != NO_LAYER_COLOR) {
            Canvas canvas = new Canvas(blurBitmap);
            canvas.drawColor(maskColor);
        }

        return blurBitmap;
    }

    /**
     * 获取模糊半径
     */
    @SuppressWarnings("unused")
    public int getRadius() {
        return radius;
    }

    /**
     * 获取图层颜色
     */
    @SuppressWarnings("unused")
    public int getMaskColor() {
        return maskColor;
    }

    @NonNull
    @Override
    public String onToString() {
        return String.format("%s(radius=%d,maskColor=%d)", "GaussianBlurImageProcessor", radius, maskColor);
    }

    @Override
    public String onGetKey() {
        return String.format("%s(radius=%d,maskColor=%d)", "GaussianBlur", radius, maskColor);
    }
}