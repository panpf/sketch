/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.shaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import me.xiaopan.sketch.request.ShapeSize;

/**
 * 圆角矩形的图片，还可以有描边
 */
@SuppressWarnings("unused")
public class RoundRectImageShaper implements ImageShaper {
    private float[] outerRadii;
    private Rect boundsCached = new Rect();

    private Path bitmapPath = new Path();
    private Path strokePath;

    private int strokeWidth;
    private int strokeColor;

    private Paint strokePaint;

    public RoundRectImageShaper(float[] radiis) {
        if (radiis == null || radiis.length < 8) {
            throw new ArrayIndexOutOfBoundsException("outer radii must have >= 8 values");
        }
        this.outerRadii = radiis;
    }

    public RoundRectImageShaper(float topLeftRadii, float topRightRadii, float bottomLeftRadii, float bottomRightRadii) {
        this(new float[]{topLeftRadii, topLeftRadii, topRightRadii, topRightRadii, bottomLeftRadii, bottomLeftRadii, bottomRightRadii, bottomRightRadii});
    }

    public RoundRectImageShaper(float radii) {
        this(radii, radii, radii, radii);
    }

    @SuppressWarnings("unused")
    public int getStrokeColor() {
        return strokeColor;
    }

    @SuppressWarnings("unused")
    public RoundRectImageShaper setStroke(int strokeColor, int strokeWidth) {
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
        if (hasStroke()) {
            if (strokePaint == null) {
                strokePaint = new Paint();
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setAntiAlias(true);
            }

            strokePaint.setColor(strokeColor);
            strokePaint.setStrokeWidth(strokeWidth);

            if (strokePath == null) {
                strokePath = new Path();
            }
        }
    }

    private boolean hasStroke() {
        return strokeColor != 0 && strokeWidth > 0;
    }


    @Override
    public void onUpdateShaderMatrix(Matrix matrix, Rect bounds, int bitmapWidth, int bitmapHeight,
                                     ShapeSize shapeSize, Rect srcRect) {

    }

    @Override
    public void draw(Canvas canvas, Paint paint, Rect bounds) {
        if (!boundsCached.equals(bounds)) {
            RectF rectF = new RectF();

            rectF.set(bounds);
            bitmapPath.reset();
            bitmapPath.addRoundRect(rectF, outerRadii, Path.Direction.CW);

            // 假如描边宽度是10，那么会是5个像素在图片外面，5哥像素在图片里面，
            // 所以往图片里面偏移描边宽度的一半，让描边都在图片里面
            if (hasStroke()) {
                final float offset = strokeWidth / 2f;
                rectF.set(bounds.left + offset, bounds.top + offset,
                        bounds.right - offset, bounds.bottom - offset);
                strokePath.reset();
                strokePath.addRoundRect(rectF, outerRadii, Path.Direction.CW);
            }
        }

        paint.setAntiAlias(true);
        canvas.drawPath(bitmapPath, paint);

        if (hasStroke() && strokePaint != null) {
            canvas.drawPath(strokePath, strokePaint);
        }
    }
}
