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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.request.Resize;

/**
 * 旋转图片处理器
 */
@SuppressWarnings("unused")
public class RotateImageProcessor extends WrappedImageProcessor {

    private int degrees;

    /**
     * 创建一个图片旋转处理器
     *
     * @param degrees               旋转角度
     * @param wrappedImageProcessor 嵌套一个图片处理器
     */
    public RotateImageProcessor(int degrees, WrappedImageProcessor wrappedImageProcessor) {
        super(wrappedImageProcessor);
        this.degrees = degrees;
    }

    /**
     * 创建一个图片旋转处理器
     *
     * @param degrees 旋转角度
     */
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
        Bitmap.Config config = bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
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

    @NonNull
    @Override
    public Bitmap onProcess(@NonNull Sketch sketch, @NonNull Bitmap bitmap, Resize resize, boolean lowQualityImage) {
        if (bitmap.isRecycled() || degrees % 360 == 0) {
            return bitmap;
        }

        return rotate(bitmap, degrees, sketch.getConfiguration().getBitmapPool());
    }

    @NonNull
    @Override
    public String onToString() {
        return String.format("%s(%d)", "RotateImageProcessor", degrees);
    }

    @Nullable
    @Override
    public String onGetKey() {
        // 0 度或 360 度时不加标识，这样做是为了避免浪费合适的内存缓存
        if (degrees % 360 == 0) {
            return null;
        }

        return String.format("%s(%d)", "Rotate", degrees);
    }
}
