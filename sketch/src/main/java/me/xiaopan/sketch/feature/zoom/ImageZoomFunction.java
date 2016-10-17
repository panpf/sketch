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

package me.xiaopan.sketch.feature.zoom;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.SketchImageView;

/**
 * ImageView缩放功能
 */
public class ImageZoomFunction extends SketchImageView.Function {
    private ImageView imageView;

    private ImageZoomer imageZoomer;
    private boolean fromLargeImageFunction;

    public ImageZoomFunction(ImageView imageView) {
        this.imageView = imageView;
        init();
    }

    private void init() {
        ImageZoomer oldImageZoomer = imageZoomer;

        imageZoomer = new ImageZoomer(imageView, true);

        if (oldImageZoomer != null) {
            oldImageZoomer.cleanup();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (imageZoomer != null) {
            imageZoomer.draw(canvas);
        }
    }

    @Override
    public void onAttachedToWindow() {
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return imageZoomer != null && imageZoomer.onTouch(imageView, event);
    }

    @Override
    public boolean onDetachedFromWindow() {
        recycle();
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if (imageZoomer != null) {
            imageZoomer.update();
        }
        return false;
    }

    @Override
    public void onSizeChanged(int left, int top, int right, int bottom) {
        if (imageZoomer != null) {
            imageZoomer.update();
        }
    }

    public ImageView.ScaleType getScaleType() {
        return imageZoomer != null ? imageZoomer.getScaleType() : null;
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        super.setScaleType(scaleType);
        if (imageZoomer != null) {
            imageZoomer.setScaleType(scaleType);
        }
    }

    public void recycle() {
        if (imageZoomer != null) {
            imageZoomer.cleanup();
            imageZoomer = null;
        }
    }

    public ImageZoomer getImageZoomer() {
        return imageZoomer;
    }

    public boolean isFromLargeImageFunction() {
        return fromLargeImageFunction;
    }

    public void setFromLargeImageFunction(boolean fromLargeImageFunction) {
        this.fromLargeImageFunction = fromLargeImageFunction;
    }
}
