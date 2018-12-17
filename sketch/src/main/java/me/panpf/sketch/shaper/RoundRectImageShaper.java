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
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.request.ShapeSize;

/**
 * 圆角矩形的绘制时图片整形器，还可以有描边
 */
@SuppressWarnings("unused")
public class RoundRectImageShaper implements ImageShaper {
    private float[] outerRadii;
    private Rect boundsCached = new Rect();

    private Path bitmapPath = new Path();
    private Path innerStrokePath;
    private Path outerStrokePath;
    private Path clipPath;

    private int strokeWidth;
    private int strokeColor;

    private Paint strokePaint;

    private Rect boundsBack;
    private RectF rectF;
    private Path path;

    public RoundRectImageShaper(float[] radiis) {
        if (radiis == null || radiis.length < 8) {
            throw new ArrayIndexOutOfBoundsException("outer radii must have >= 8 values");
        }
        this.outerRadii = radiis;
    }

    public RoundRectImageShaper(float topLeftRadii, float topRightRadii, float bottomLeftRadii, float bottomRightRadii) {
        this(new float[]{topLeftRadii, topLeftRadii, topRightRadii, topRightRadii,
                bottomLeftRadii, bottomLeftRadii, bottomRightRadii, bottomRightRadii});
    }

    public RoundRectImageShaper(float radii) {
        this(radii, radii, radii, radii);
    }

    public float[] getOuterRadii() {
        return outerRadii;
    }

    @SuppressWarnings("unused")
    public int getStrokeColor() {
        return strokeColor;
    }

    @SuppressWarnings("unused")
    @NonNull
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

            if (innerStrokePath == null) {
                innerStrokePath = new Path();
            }

            if (outerStrokePath == null) {
                outerStrokePath = new Path();
            }

            if (clipPath == null) {
                clipPath = new Path();
            }
        }
    }

    private boolean hasStroke() {
        return strokeColor != 0 && strokeWidth > 0;
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

        if (rectF == null) {
            rectF = new RectF();
        }
        rectF.set(boundsBack);

        path.addRoundRect(rectF, outerRadii, Path.Direction.CW);

        return path;
    }


    @Override
    public void onUpdateShaderMatrix(@NonNull Matrix matrix, @NonNull Rect bounds, int bitmapWidth, int bitmapHeight,
                                     @Nullable ShapeSize shapeSize, @NonNull Rect srcRect) {

    }

    @Override
    public void draw(@NonNull Canvas canvas, @NonNull Paint paint, @NonNull Rect bounds) {
        if (!boundsCached.equals(bounds)) {
            RectF rectF = new RectF(bounds);

            bitmapPath.reset();
            bitmapPath.addRoundRect(rectF, outerRadii, Path.Direction.CW);

            // 假如描边宽度是10，那么会是5个像素在图片外面，5个像素在图片里面
            // 因为描边会有一半是在图片外面，所以如果图片被紧紧（没有缝隙）包括在Layout中，那么描边就会丢失一半
            if (hasStroke()) {
                // 内圈，往图片里面偏移描边宽度的一半，让描边都在图片里面，都在里面导致圆角部分会露出来一些
                final float offset = strokeWidth / 2f;
                rectF.set(bounds.left + offset, bounds.top + offset,
                        bounds.right - offset, bounds.bottom - offset);
                innerStrokePath.reset();
                innerStrokePath.addRoundRect(rectF, outerRadii, Path.Direction.CW);

                // 外圈，主要用来盖住内圈描边无法覆盖导致露出的圆角部分
                outerStrokePath.reset();
                rectF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
                outerStrokePath.addRoundRect(rectF, outerRadii, Path.Direction.CW);

                rectF.set(bounds);
                clipPath.addRoundRect(rectF, outerRadii, Path.Direction.CW);
            }
        }

        paint.setAntiAlias(true);
        canvas.drawPath(bitmapPath, paint);

        if (hasStroke() && strokePaint != null) {
            // 裁掉外圈跑出图片的部分
            canvas.clipPath(clipPath);

            canvas.drawPath(innerStrokePath, strokePaint);
            canvas.drawPath(outerStrokePath, strokePaint);
        }
    }
}
