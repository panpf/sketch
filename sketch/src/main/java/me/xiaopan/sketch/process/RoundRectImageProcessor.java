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
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.Resize;

/**
 * 圆角矩形图片处理器
 */
@SuppressWarnings("unused")
public class RoundRectImageProcessor extends WrappedImageProcessor {
    protected String logName = "RoundRectImageProcessor";

    private float[] cornerRadius;

    public RoundRectImageProcessor(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius, WrappedImageProcessor wrappedImageProcessor) {
        super(wrappedImageProcessor);
        cornerRadius = new float[]{topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomLeftRadius, bottomLeftRadius,
                bottomRightRadius, bottomRightRadius};
    }

    public RoundRectImageProcessor(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        this(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, null);
    }

    public RoundRectImageProcessor(float cornerRadius, WrappedImageProcessor wrappedImageProcessor) {
        this(cornerRadius, cornerRadius, cornerRadius, cornerRadius, wrappedImageProcessor);
    }

    public RoundRectImageProcessor(float cornerRadius) {
        this(cornerRadius, cornerRadius, cornerRadius, cornerRadius, null);
    }

    @Override
    public String onGetKey() {
        if (cornerRadius != null) {
            return String.format("%s(cornerRadius=[%sx%s,%sx%s,%sx%s,%sx%s])",
                    logName, cornerRadius[0], cornerRadius[1], cornerRadius[2], cornerRadius[3],
                    cornerRadius[4], cornerRadius[5], cornerRadius[6], cornerRadius[7]);
        } else {
            return logName;
        }
    }

    @Override
    protected boolean isInterceptResize() {
        return true;
    }

    @Override
    public Bitmap onProcess(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        ResizeCalculator resizeCalculator = sketch.getConfiguration().getResizeCalculator();
        ResizeCalculator.Result result = resizeCalculator.calculator(bitmap.getWidth(), bitmap.getHeight(),
                resize != null ? resize.getWidth() : bitmap.getWidth(),
                resize != null ? resize.getHeight() : bitmap.getHeight(),
                resize != null ? resize.getScaleType() : null, forceUseResize);
        if (result == null) {
            return bitmap;
        }

        Bitmap.Config config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap roundRectBitmap = bitmapPool.getOrMake(result.imageWidth, result.imageHeight, config);

        Canvas canvas = new Canvas(roundRectBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        // 绘制圆角的罩子
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, result.imageWidth, result.imageHeight), cornerRadius, Path.Direction.CW);
        canvas.drawPath(path, paint);

        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, paint);

        return roundRectBitmap;
    }

    public float[] getCornerRadius() {
        return cornerRadius;
    }
}
