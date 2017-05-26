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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.Resize;

/**
 * 旋转图片处理器
 */
@SuppressWarnings("unused")
public class RotateImageProcessor extends WrappedImageProcessor {
    private static final String KEY = "RotateImageProcessor";

    private int degrees;

    public RotateImageProcessor(int degrees, WrappedImageProcessor wrappedImageProcessor) {
        super(wrappedImageProcessor);
        this.degrees = degrees;
    }

    public RotateImageProcessor(int degrees) {
        this(degrees, null);
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees, BitmapPool bitmapPool) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);

        // 根据旋转角度计算新的图片的尺寸
        RectF newRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        matrix.mapRect(newRect);
        int newWidth = (int) newRect.width();
        int newHeight = (int) newRect.height();

        // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
        Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : null;
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap result = bitmapPool.getOrMake(newWidth, newHeight, config);

        matrix.postTranslate(-newRect.left, -newRect.top);

        final Canvas canvas = new Canvas(result);
        final Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, matrix, paint);

        return result;
    }

    @Override
    public Bitmap onProcess(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled() || degrees % 360 == 0) {
            return bitmap;
        }

        return rotate(bitmap, degrees, sketch.getConfiguration().getBitmapPool());
    }

    @Override
    public String onGetKey() {
        // 0度或360度时不加标识，这样做是为了避免浪费合适的内存缓存
        if (degrees % 360 == 0) {
            return null;
        } else {
            return String.format("%s(degrees=%d)", KEY, degrees);
        }
    }
}
