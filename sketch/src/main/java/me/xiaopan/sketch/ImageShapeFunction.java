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

package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import me.xiaopan.sketch.SketchImageView.ImageShape;

public class ImageShapeFunction implements ImageViewFunction{

    protected Path clipPath;
    protected int roundedRadius;
    protected ImageShape imageShape = ImageShape.RECT;
    protected RectF rectF;

    private View view;

    public ImageShapeFunction(View view) {
        this.view = view;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initImageShapePath();
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        return false;
    }

    protected void initImageShapePath() {
        if (imageShape == ImageShape.RECT) {
            clipPath = null;
        } else if (imageShape == ImageShape.CIRCLE) {
            if (clipPath == null) {
                clipPath = new Path();
            } else {
                clipPath.reset();
            }
            int xRadius = (view.getWidth() - view.getPaddingLeft() - view.getPaddingRight()) / 2;
            int yRadius = (view.getHeight() - view.getPaddingTop() - view.getPaddingBottom()) / 2;
            clipPath.addCircle(xRadius, yRadius, xRadius < yRadius ? xRadius : yRadius, Path.Direction.CW);
        } else if (imageShape == ImageShape.ROUNDED_RECT) {
            if (clipPath == null) {
                clipPath = new Path();
            } else {
                clipPath.reset();
            }
            if (rectF == null) {
                rectF = new RectF(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
            } else {
                rectF.set(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
            }
            clipPath.addRoundRect(rectF, roundedRadius, roundedRadius, Path.Direction.CW);
        } else {
            clipPath = null;
        }
    }

    @SuppressWarnings("unused")
    public ImageShape getImageShape() {
        return imageShape;
    }

    public void setImageShape(ImageShape imageShape) {
        this.imageShape = imageShape;
        if (view.getWidth() != 0) {
            initImageShapePath();
        }
    }

    @SuppressWarnings("unused")
    public int getRoundedRadius() {
        return roundedRadius;
    }

    public void setRoundedRadius(int radius) {
        this.roundedRadius = radius;
        if (view.getWidth() != 0) {
            initImageShapePath();
        }
    }

    public Path getClipPath() {
        return clipPath;
    }
}
