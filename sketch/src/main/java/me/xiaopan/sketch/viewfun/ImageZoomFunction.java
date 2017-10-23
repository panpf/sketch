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

package me.xiaopan.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.xiaopan.sketch.zoom.ImageZoomer;
import me.xiaopan.sketch.zoom.huge.HugeImageViewer;

/**
 * ImageView 缩放功能
 */
public class ImageZoomFunction extends ViewFunction {

    private ImageZoomer imageZoomer;

    public ImageZoomFunction(FunctionPropertyView view) {
        this.imageZoomer = new ImageZoomer(view);
    }

    @Override
    public void onAttachedToWindow() {
        imageZoomer.reset();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        imageZoomer.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return imageZoomer.onTouchEvent(event);
    }

    @Override
    public boolean onDrawableChanged(@NonNull String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        imageZoomer.reset();
        return false;
    }

    @Override
    public void onSizeChanged(int left, int top, int right, int bottom) {
        imageZoomer.reset();
    }

    @Override
    public boolean onDetachedFromWindow() {
        recycle("onDetachedFromWindow");
        return false;
    }

    public ImageView.ScaleType getScaleType() {
        return imageZoomer.getScaleType();
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        imageZoomer.setScaleType(scaleType);
    }

    public void recycle(String why) {
        imageZoomer.recycle(why);
    }

    public ImageZoomer getImageZoomer() {
        return imageZoomer;
    }

    @SuppressWarnings("unused")
    public HugeImageViewer getHugeImageViewer() {
        return imageZoomer.getHugeImageViewer();
    }
}
