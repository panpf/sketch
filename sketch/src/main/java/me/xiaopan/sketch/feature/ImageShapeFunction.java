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

package me.xiaopan.sketch.feature;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.SketchImageView.ImageShape;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.UriScheme;

/**
 * 定制图片形状功能，可以设置图片的形状，然后下载进度和按下效果蒙层就会根据此形状改变形状，以保证蒙层和图片的形状锲合
 */
public class ImageShapeFunction implements SketchImageView.Function {

    protected Path clipPath;
    protected float[] cornerRadius;
    protected ImageShape imageShape = ImageShape.RECT;
    protected RectF rectF;

    private View view;

    public ImageShapeFunction(View view) {
        this.view = view;
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public boolean onDisplay(UriScheme uriScheme) {
        return false;
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
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDetachedFromWindow() {
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
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

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
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
            clipPath.addRoundRect(rectF, cornerRadius, Path.Direction.CW);
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
    public float[] getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        setCornerRadius(cornerRadius, cornerRadius, cornerRadius, cornerRadius);
    }

    public void setCornerRadius(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        this.cornerRadius = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius};
        if (view.getWidth() != 0) {
            initImageShapePath();
        }
    }

    public Path getClipPath() {
        return clipPath;
    }
}
