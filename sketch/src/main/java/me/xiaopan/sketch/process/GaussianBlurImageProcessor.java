package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import me.xiaopan.sketch.Resize;
import me.xiaopan.sketch.Sketch;

/**
 * 高斯模糊图片处理器
 */
public class GaussianBlurImageProcessor extends DefaultImageProcessor {
    private static final String NAME = "GaussianBlurImageProcessor";

    private int radius = 15;
    private boolean isDarkHandle;

    /**
     * @param radius 模糊半径，取值为0到100，默认为15
     * @param isDarkHandle 是否让模糊后的图片看起来更暗一些，实现原理就是加上一层#88000000颜色。常用于页面背景，因为太亮的背景会影响页面上展示的内容，默认为false
     */
    public GaussianBlurImageProcessor(int radius, boolean isDarkHandle) {
        this.radius = radius;
        this.isDarkHandle = isDarkHandle;
    }

    /**
     * @param radius 模糊半径，取值为0到100，默认为15
     */
    public GaussianBlurImageProcessor(int radius) {
        this.radius = radius;
    }

    /**
     * @param isDarkHandle 是否让模糊后的图片看起来更暗一些，实现原理就是加上一层#88000000颜色。常用于页面背景，因为太亮的背景会影响页面上展示的内容，默认为false
     */
    public GaussianBlurImageProcessor(boolean isDarkHandle) {
        this.isDarkHandle = isDarkHandle;
    }

    public GaussianBlurImageProcessor() {
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        builder.append(NAME);
        builder.append(" - ");
        builder.append("radius");
        builder.append("=");
        builder.append(radius);
        builder.append(";");
        builder.append("isDarkHandle");
        builder.append("=");
        builder.append(isDarkHandle);
        return builder;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(new StringBuilder()).toString();
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        // cut handle
        Bitmap resizeBitmap = super.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);
        if(resizeBitmap == null){
            return null;
        }

        // blur handle
        Bitmap blurBitmap= fastGaussianBlur(resizeBitmap, radius, false, lowQualityImage);
        if(resizeBitmap != bitmap){
            resizeBitmap.recycle();
        }

        // dark handle
        if(blurBitmap != null && isDarkHandle){
            Canvas canvas = new Canvas(blurBitmap);
            canvas.drawColor(Color.parseColor("#55000000"));
        }

        return blurBitmap;
    }

    /**
     * 快速高斯模糊
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
    public Bitmap fastGaussianBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap, boolean lowQualityImage) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        try{
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
        }catch(Throwable throwable){
            throwable.printStackTrace();
            if(bitmap != null && bitmap != sentBitmap){
                bitmap.recycle();
            }
            return null;
        }
    }
}