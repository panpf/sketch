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
import android.text.TextUtils;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.request.Resize;

/**
 * 圆角图片处理器
 */
public class RoundedCornerImageProcessor implements ImageProcessor {
    protected String logName = "RoundedCornerImageProcessor";

    private float[] cornerRadius;

    public RoundedCornerImageProcessor(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        cornerRadius = new float[]{topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomLeftRadius, bottomLeftRadius,
                bottomRightRadius, bottomRightRadius};
    }

    public RoundedCornerImageProcessor(float cornerRadius) {
        this(cornerRadius, cornerRadius, cornerRadius, cornerRadius);
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
        builder.append(logName);
        if (cornerRadius != null) {
            builder.append("(")
                    .append("cornerRadius=[")
                    .append(cornerRadius[0]).append("x").append(cornerRadius[1])
                    .append(",")
                    .append(cornerRadius[2]).append("x").append(cornerRadius[3])
                    .append(",")
                    .append(cornerRadius[4]).append("x").append(cornerRadius[5])
                    .append(",")
                    .append(cornerRadius[6]).append("x").append(cornerRadius[7])
                    .append("]")
                    .append(")");
        }
        return builder;
    }

    @Override
    public Bitmap process(Sketch sketch, Bitmap bitmap, Resize resize, boolean forceUseResize, boolean lowQualityImage) {
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

        Bitmap output = Bitmap.createBitmap(result.imageWidth, result.imageHeight,
                lowQualityImage ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
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
        return output;
    }

    public float[] getCornerRadius() {
        return cornerRadius;
    }
}
