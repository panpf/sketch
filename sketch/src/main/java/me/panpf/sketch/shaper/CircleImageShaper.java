/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.shaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.request.ShapeSize;

/**
 * 圆形的绘制时图片整形器，还可以有描边
 */
public class CircleImageShaper implements ImageShaper {
    private int strokeWidth;
    private int strokeColor;

    private Paint strokePaint;
    private Rect boundsBack;
    private Path path;

    @SuppressWarnings("unused")
    public int getStrokeColor() {
        return strokeColor;
    }

    @SuppressWarnings("unused")
    @NonNull
    public CircleImageShaper setStroke(int strokeColor, int strokeWidth) {
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        updatePaint();
        return this;
    }

    @SuppressWarnings("unused")
    public int getStrokeWidth() {
        return strokeWidth;
    }

    private void updatePaint() {
        if (strokeColor != 0 && strokeWidth > 0) {
            if (strokePaint == null) {
                strokePaint = new Paint();
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setAntiAlias(true);
            }

            strokePaint.setColor(strokeColor);
            strokePaint.setStrokeWidth(strokeWidth);
        }
    }

    @NonNull
    @Override
    public Path getPath(@NonNull Rect bounds) {
        if (path != null && boundsBack != null && boundsBack.equals(bounds)) {
            return path;
        }

        if (boundsBack == null) {
            boundsBack = new Rect();
        }
        boundsBack.set(bounds);

        if (path == null) {
            path = new Path();
        }
        path.reset();

        int centerX = bounds.centerX();
        int centerY = bounds.centerX();
        int radius = Math.max(centerX - bounds.left, centerY - bounds.top);
        path.addCircle(centerX, centerY, radius, Path.Direction.CW);

        return path;
    }

    @Override
    public void onUpdateShaderMatrix(@NonNull Matrix matrix, @NonNull Rect bounds, int bitmapWidth, int bitmapHeight,
                                     @Nullable ShapeSize shapeSize, @NonNull Rect srcRect) {

    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint, @NonNull Rect bounds) {
        final float widthRadius = bounds.width() / 2f;
        final float heightRadius = bounds.height() / 2f;
        final float cx = bounds.left + widthRadius;
        final float cy = bounds.top + heightRadius;
        final float radius = Math.min(widthRadius, heightRadius);

        paint.setAntiAlias(true);
        canvas.drawCircle(cx, cy, radius, paint);

        if (strokeColor != 0 && strokeWidth > 0 && strokePaint != null) {
            // 假如描边宽度是10，那么会是5个像素在图片外面，5哥像素在图片里面，
            // 所以往图片里面偏移描边宽度的一半，让描边都在图片里面
            final float offset = strokeWidth / 2f;
            canvas.drawCircle(cx, cy, radius - offset, strokePaint);
        }
    }
}
