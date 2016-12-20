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
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 倒影图片处理器
 */
@SuppressWarnings("unused")
public class ReflectionImageProcessor extends ResizeImageProcessor {
    protected String logName = "ReflectionImageProcessor";

    private int reflectionSpacing;
    private float reflectionScale;

    /**
     * 创建一个倒影图片处理器
     *
     * @param reflectionSpacing 倒影和图片之间的距离
     * @param reflectionScale   倒影的高度所占原图高度比例，取值为0.0到1
     */
    public ReflectionImageProcessor(int reflectionSpacing, float reflectionScale) {
        this.reflectionSpacing = reflectionSpacing;
        this.reflectionScale = reflectionScale;
    }

    public ReflectionImageProcessor() {
        this(2, 0.3f);
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName)
                .append("(")
                .append("scale=").append(reflectionScale)
                .append(",")
                .append("spacing=").append(reflectionSpacing)
                .append(")");
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        // 先resize
        Bitmap srcBitmap = super.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);

        int finalHeight = (int) (srcBitmap.getHeight() + reflectionSpacing + (srcBitmap.getHeight() * reflectionScale));
        Bitmap.Config config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        // 创建新图片
        Bitmap newBitmap = bitmapPool.getOrMake(srcBitmap.getWidth(), finalHeight, config);
        Canvas canvas = new Canvas(newBitmap);

        // 在上半部分绘制原图
        canvas.drawBitmap(srcBitmap, 0, 0, null);

        // 创建一个180度翻转的图片作为倒影
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
        if (srcBitmap != bitmap) {
            SketchUtils.freeBitmapToPool(srcBitmap, bitmapPool);
        }

        // 在下半部分绘制倒影
        canvas.drawBitmap(reflectionImage, 0, srcBitmap.getHeight() + reflectionSpacing, null);
        SketchUtils.freeBitmapToPool(reflectionImage, bitmapPool);

        // 在下半部分绘制半透明遮罩
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, srcBitmap.getHeight() + reflectionSpacing,
                0, newBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, srcBitmap.getHeight() + reflectionSpacing,
                newBitmap.getWidth(), newBitmap.getHeight(), paint);

        return newBitmap;
    }

    public float getReflectionScale() {
        return reflectionScale;
    }

    public void setReflectionScale(float reflectionScale) {
        this.reflectionScale = reflectionScale;
    }

    public int getReflectionSpacing() {
        return reflectionSpacing;
    }

    public void setReflectionSpacing(int reflectionSpacing) {
        this.reflectionSpacing = reflectionSpacing;
    }
}
