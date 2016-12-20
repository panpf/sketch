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
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.request.Resize;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 旋转图片处理器
 */
@SuppressWarnings("unused")
public class RotateImageProcessor extends ResizeImageProcessor {
    protected String logName = "RotateImageProcessor";

    private int degrees;

    public RotateImageProcessor(int degrees) {
        this.degrees = degrees;
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        Bitmap resizeBitmap = super.process(sketch, bitmap, resize, forceUseResize, lowQualityImage);

        if (degrees == 0) {
            return resizeBitmap;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);

        // 根据旋转角度计算新的图片的尺寸
        RectF dstR = new RectF(0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight());
        RectF deviceR = new RectF();
        matrix.mapRect(deviceR, dstR);
        int newWidth = (int) deviceR.width();
        int newHeight = (int) deviceR.height();

        // 创建新图片
        BitmapPool bitmapPool = sketch.getConfiguration().getBitmapPool();
        Bitmap.Config config = resizeBitmap.getConfig() != null ? resizeBitmap.getConfig() : null;
        if (degrees % 90 != 0 && config != Bitmap.Config.ARGB_8888) {   // 角度不能整除90°时新图片会是斜的，因此要支持透明度，这样倾斜导致露出的部分就不会是黑的
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap rotateBitmap = bitmapPool.getOrMake(newWidth, newHeight, config);

        // 绘制
        Canvas canvas = new Canvas(rotateBitmap);
        canvas.translate(-deviceR.left, -deviceR.top);
        canvas.concat(matrix);
        Paint paint = new Paint();

        Rect srcR = new Rect(0, 0, resizeBitmap.getWidth(), resizeBitmap.getHeight());
        canvas.drawBitmap(resizeBitmap, srcR, dstR, paint);
        canvas.setBitmap(null);

        if (resizeBitmap != bitmap) {
            SketchUtils.freeBitmapToPool(resizeBitmap, sketch.getConfiguration().getBitmapPool());
        }

        return rotateBitmap;
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        // 0度或360度时不加标识，这样做是为了避免浪费合适的内存缓存
        if (degrees % 360 == 0) {
            return builder;
        }

        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName)
                .append("(")
                .append("degrees=").append(degrees)
                .append(")");
    }
}
