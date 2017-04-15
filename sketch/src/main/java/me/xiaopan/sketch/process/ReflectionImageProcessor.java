/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.Resize;

/**
 * 倒影图片处理器
 */
@SuppressWarnings("unused")
public class ReflectionImageProcessor extends WrappedImageProcessor {
    private static final int DEFAULT_REFLECTION_SPACING = 2;
    private static final float DEFAULT_REFLECTION_SCALE = 0.3f;

    protected String logName = "ReflectionImageProcessor";
    private int reflectionSpacing;
    private float reflectionScale;

    /**
     * 创建一个倒影图片处理器
     *
     * @param reflectionSpacing      倒影和图片之间的距离
     * @param reflectionScale        倒影的高度所占原图高度比例，取值为0.0到1
     * @param wrappedImageProcessor 嵌套一个图片处理器
     */
    public ReflectionImageProcessor(int reflectionSpacing, float reflectionScale, WrappedImageProcessor wrappedImageProcessor) {
        super(wrappedImageProcessor);
        this.reflectionSpacing = reflectionSpacing;
        this.reflectionScale = reflectionScale;
    }

    /**
     * 创建一个倒影图片处理器
     *
     * @param reflectionSpacing 倒影和图片之间的距离
     * @param reflectionScale   倒影的高度所占原图高度比例，取值为0.0到1
     */
    public ReflectionImageProcessor(int reflectionSpacing, float reflectionScale) {
        this(reflectionSpacing, reflectionScale, null);
    }

    public ReflectionImageProcessor(WrappedImageProcessor wrappedImageProcessor) {
        this(DEFAULT_REFLECTION_SPACING, DEFAULT_REFLECTION_SCALE, wrappedImageProcessor);
    }

    public ReflectionImageProcessor() {
        this(DEFAULT_REFLECTION_SPACING, DEFAULT_REFLECTION_SCALE, null);
    }

    @Override
    public String onGetKey() {
        return String.format("%s(scale=%s,spacing=%d)", logName, reflectionScale, reflectionSpacing);
    }

    @Override
    public Bitmap onProcess(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return bitmap;
        }

        int srcHeight = bitmap.getHeight();
        int reflectionHeight = (int) (srcHeight * reflectionScale);
        int reflectionTop = srcHeight + reflectionSpacing;

        Bitmap.Config config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap reflectionBitmap = bitmapPool.getOrMake(bitmap.getWidth(), reflectionTop + reflectionHeight, config);

        // 在上半部分绘制原图
        Canvas canvas = new Canvas(reflectionBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        // 在下半部分绘制倒影
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1);
        matrix.postTranslate(0, srcHeight + reflectionTop);
        canvas.drawBitmap(bitmap, matrix, null);

        // 在倒影部分绘制半透明遮罩，让倒影部分产生半透明渐变的效果
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, reflectionTop, 0, reflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, reflectionTop, reflectionBitmap.getWidth(), reflectionBitmap.getHeight(), paint);

        return reflectionBitmap;
    }

    public float getReflectionScale() {
        return reflectionScale;
    }

    public int getReflectionSpacing() {
        return reflectionSpacing;
    }
}
