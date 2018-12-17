/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.process;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ResizeCalculator;
import me.panpf.sketch.request.Resize;

/**
 * 圆形图片处理器
 */
@SuppressWarnings("unused")
public class CircleImageProcessor extends WrappedImageProcessor {

    private static CircleImageProcessor instance;

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
    protected boolean isInterceptResize() {
        return true;
    }

    @NonNull
    @Override
    public Bitmap onProcess(@NonNull Sketch sketch, @NonNull Bitmap bitmap, @Nullable Resize resize, boolean lowQualityImage) {
        if (bitmap.isRecycled()) {
            return bitmap;
        }

        int targetWidth = resize != null ? resize.getWidth() : bitmap.getWidth();
        int targetHeight = resize != null ? resize.getHeight() : bitmap.getHeight();
        int newBitmapSize = targetWidth < targetHeight ? targetWidth : targetHeight;
        ImageView.ScaleType scaleType = resize != null ? resize.getScaleType() : ImageView.ScaleType.FIT_CENTER;

        ResizeCalculator resizeCalculator = sketch.getConfiguration().getResizeCalculator();
        ResizeCalculator.Mapping mapping = resizeCalculator.calculator(bitmap.getWidth(), bitmap.getHeight(),
                newBitmapSize, newBitmapSize, scaleType, resize != null && resize.getMode() == Resize.Mode.EXACTLY_SAME);
        if (mapping == null) {
            return bitmap;
        }

        Bitmap.Config config = lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888;
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();

        Bitmap circleBitmap = bitmapPool.getOrMake(mapping.imageWidth, mapping.imageHeight, config);

        Canvas canvas = new Canvas(circleBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFFFF0000);

        // 绘制圆形的罩子
        canvas.drawCircle(mapping.imageWidth / 2, mapping.imageHeight / 2,
                (mapping.imageWidth < mapping.imageHeight ? mapping.imageWidth : mapping.imageHeight) / 2, paint);

        // 应用遮罩模式并绘制图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, mapping.srcRect, mapping.destRect, paint);

        return circleBitmap;
    }

    @NonNull
    @Override
    public String onToString() {
        return "CircleImageProcessor";
    }

    @Override
    public String onGetKey() {
        return "Circle";
    }
}
