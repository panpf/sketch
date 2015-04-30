package me.xiaopan.spear.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

import me.xiaopan.spear.ImageSize;

/**
 * 高斯模糊图片处理器
 */
public class GaussianBlurImageProcessor extends CutImageProcessor {
    private float horizontalRadius = 15; // 水平方向模糊度
    private float verticalRadius = 15; // 竖直方向模糊度
    private int iterations = 1; // 模糊迭代度
    private boolean isDarkHandle;

    private String tag;

    /**
     * @param horizontalRadius 水平方向模糊半径，取值为0到100，默认为15
     * @param verticalRadius 垂直方向模糊半径，取值为0到100，默认为15
     * @param iterations 模糊迭代次数，数字越大模糊效果越重，默认为1
     * @param isDarkHandle 是否让模糊后的图片看起来更暗一些，实现原理就是加上一层#88000000颜色。常用于页面背景，因为太亮的背景会影响页面上展示的内容，默认为false
     */
    public GaussianBlurImageProcessor(int horizontalRadius, int verticalRadius, int iterations, boolean isDarkHandle) {
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
        this.iterations = iterations;
        this.isDarkHandle = isDarkHandle;
    }

    /**
     * @param radius 模糊半径，取值为0到100，默认为15
     * @param isDarkHandle 是否让模糊后的图片看起来更暗一些，实现原理就是加上一层#88000000颜色。常用于页面背景，因为太亮的背景会影响页面上展示的内容，默认为false
     */
    public GaussianBlurImageProcessor(int radius, boolean isDarkHandle) {
        this.horizontalRadius = radius;
        this.verticalRadius = radius;
        this.isDarkHandle = isDarkHandle;
    }

    /**
     * @param radius 模糊半径，取值为0到100，默认为15
     */
    public GaussianBlurImageProcessor(int radius) {
        this.horizontalRadius = radius;
        this.verticalRadius = radius;
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
    public Bitmap process(Bitmap bitmap, ImageSize resize, ImageView.ScaleType scaleType) {
        Bitmap resizeBitmap = super.process(bitmap, resize, scaleType);
        Bitmap blurBitmap = gaussianBlurFilter(resizeBitmap, horizontalRadius, verticalRadius, iterations);

        if (resizeBitmap != bitmap) {
            resizeBitmap.recycle();
        }

        if (isDarkHandle) {
            Canvas canvas = new Canvas(blurBitmap);
            canvas.drawColor(Color.parseColor("#88000000"));
        }
        return blurBitmap;
    }

    @Override
    public String getFlag() {
        if(tag == null){
            tag = "BlurImageProcessor(horizontalRadius=" + horizontalRadius + "; verticalRadius="+verticalRadius+"; iterations=" + iterations + ";isDarkHandle=" + isDarkHandle + ")";
        }
        return tag;
    }

    /**
     * 高斯模糊
     */
    public static Bitmap gaussianBlurFilter(Bitmap bmp, float hRadius, float vRadius, int iterations) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static void blur(int[] in, int[] out, int width, int height,
                            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++) {
            divide[i] = i / tableSize;
        }

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    private static void blurFractional(int[] in, int[] out, int width,
                                      int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    private static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }
}
