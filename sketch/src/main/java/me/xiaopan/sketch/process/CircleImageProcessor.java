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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.Resize;

/**
 * 圆形图片处理器
 */
@SuppressWarnings("unused")
public class CircleImageProcessor extends WrappedImageProcessor {
    private static CircleImageProcessor instance;

    protected String logName = "CircleImageProcessor";

    public CircleImageProcessor(WrappedImageProcessor wrappedProcessor) {
        super(wrappedProcessor);
    }

    private CircleImageProcessor() {
        this(null);
    }

    public static CircleImageProcessor getInstance() {
        if (instance == null) {
            synchronized (CircleImageProcessor.class) {
                if (instance == null) {
                    instance = new CircleImageProcessor();
                }
            }
        }
        return instance;
    }

    @Override
    public String onGetKey() {
        return logName;
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

        int targetWidth = resize != null ? resize.getWidth() : bitmap.getWidth();
        int targetHeight = resize != null ? resize.getHeight() : bitmap.getHeight();
        int newBitmapSize = targetWidth < targetHeight ? targetWidth : targetHeight;
        ImageView.ScaleType scaleType = resize != null ? resize.getScaleType() : ImageView.ScaleType.FIT_CENTER;

        ResizeCalculator resizeCalculator = sketch.getConfiguration().getResizeCalculator();
        ResizeCalculator.Result result = resizeCalculator.calculator(bitmap.getWidth(), bitmap.getHeight(),
                newBitmapSize, newBitmapSize, scaleType, forceUseResize);
        if (result == null) {
            return bitmap;
        }

        Bitmap.Config config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap circleBitmap = bitmapPool.getOrMake(result.imageWidth, result.imageHeight, config);

        Canvas canvas = new Canvas(circleBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        // 绘制圆形的罩子
        canvas.drawCircle(result.imageWidth / 2, result.imageHeight / 2,
                (result.imageWidth < result.imageHeight ? result.imageWidth : result.imageHeight) / 2, paint);

        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, result.srcRect, result.destRect, paint);

        return circleBitmap;
    }
}
